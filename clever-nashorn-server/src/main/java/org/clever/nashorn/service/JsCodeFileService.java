package org.clever.nashorn.service;

import org.apache.commons.lang3.StringUtils;
import org.clever.common.exception.BusinessException;
import org.clever.common.utils.mapper.BeanMapper;
import org.clever.common.utils.tree.BuildTreeUtils;
import org.clever.common.utils.tuples.TupleTow;
import org.clever.nashorn.dto.request.JsCodeFileAddReq;
import org.clever.nashorn.dto.request.JsCodeFileTreeFindReq;
import org.clever.nashorn.dto.request.JsCodeFileUpdateReq;
import org.clever.nashorn.entity.CodeFileHistory;
import org.clever.nashorn.entity.EnumConstant;
import org.clever.nashorn.entity.JsCodeFile;
import org.clever.nashorn.event.JsCodeFileChangeEnum;
import org.clever.nashorn.event.JsCodeFileChangeEvent;
import org.clever.nashorn.mapper.CodeFileHistoryMapper;
import org.clever.nashorn.mapper.JsCodeFileMapper;
import org.clever.nashorn.model.JsCodeFileNode;
import org.clever.nashorn.utils.JsCodeFilePathUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 作者：lizw <br/>
 * 创建时间：2019/08/27 09:34 <br/>
 */
@Transactional(readOnly = true)
@Service
public class JsCodeFileService {
    @Autowired
    private JsCodeFileMapper jsCodeFileMapper;
    @Autowired
    private CodeFileHistoryMapper codeFileHistoryMapper;
    @Autowired
    private ApplicationContext applicationContext;

    public JsCodeFile getJsCodeFile(Long id) {
        JsCodeFile jsCodeFile = jsCodeFileMapper.selectById(id);
        if (jsCodeFile != null) {
            // 发布更新事件
            applicationContext.publishEvent(new JsCodeFileChangeEvent(this, JsCodeFileChangeEnum.Update, jsCodeFile));
        }
        return jsCodeFile;
    }

    public List<JsCodeFileNode> findJsCodeFileTree(JsCodeFileTreeFindReq req) {
        final List<JsCodeFileNode> treeList = new ArrayList<>();
        List<JsCodeFile> list = jsCodeFileMapper.findByBizAndGroup(req.getBizType(), req.getGroupName());
        if (list.size() <= 0) {
            return treeList;
        }
        // // 增加根路径
        // JsCodeFile root = new JsCodeFile();
        // root.setId(-1L);
        // root.setBizType(req.getBizType());
        // root.setGroupName(req.getGroupName());
        // root.setNodeType(EnumConstant.Node_Type_2);
        // root.setFilePath("");
        // root.setName(EnumConstant.File_Path_Separator);
        // treeList.add(new JsCodeFileNode(root));
        // 增加子路径
        list.forEach(jsCodeFile -> treeList.add(new JsCodeFileNode(jsCodeFile)));
        return BuildTreeUtils.buildTree(treeList);
    }

    /**
     * 拆分路径<br />
     * <pre>
     *     ""                -> "", ""
     *     "/"               -> "/", ""
     *     "/public"         -> "/", "public"
     *     "/public/"        -> "/", "public"
     *     "/public/tmp.js"  -> "/public/", "tmp.js"
     * </pre>
     *
     * @param filePath 文件路径
     */
    private static TupleTow<String, String> getParentPath(String filePath) {
        if (filePath.endsWith(EnumConstant.File_Path_Separator)) {
            filePath = filePath.substring(0, filePath.length() - 1);
        }
        return JsCodeFilePathUtils.getParentPath(filePath);
    }

    @Transactional
    public JsCodeFile addJsCodeFile(JsCodeFileAddReq req) {
        JsCodeFile jsCodeFile = BeanMapper.mapper(req, JsCodeFile.class);
        if (Objects.equals(EnumConstant.Node_Type_2, jsCodeFile.getNodeType())) {
            jsCodeFile.setJsCode(null);
        } else {
            // 上级路径，以“/”号结尾
            jsCodeFile.setFilePath(JsCodeFilePathUtils.getFilePath(jsCodeFile.getFilePath()));
            jsCodeFile.setName(StringUtils.trim(jsCodeFile.getName()) + ".js");
        }
        // 如果父路径不是根路径，校验父路径存在
        if (!Objects.equals(EnumConstant.File_Path_Separator, jsCodeFile.getFilePath())) {
            TupleTow<String, String> tupleTow = getParentPath(req.getFilePath());
            JsCodeFile parent = jsCodeFileMapper.getJsCodeFile(req.getBizType(), req.getGroupName(), EnumConstant.Node_Type_2, tupleTow.getValue1(), tupleTow.getValue2());
            if (parent == null) {
                throw new BusinessException("父路径不存在");
            }
        }
        // 校验文件路径不重复
        JsCodeFile exists = jsCodeFileMapper.getByFullPath(req.getBizType(), req.getGroupName(), req.getFilePath(), req.getName());
        if (exists != null) {
            throw new BusinessException("与已经存在的文件" + (Objects.equals(EnumConstant.Node_Type_2, exists.getNodeType()) ? "夹" : "") + "同名，请重新指定名称");
        }
        // 保存数据
        jsCodeFileMapper.insert(jsCodeFile);
        // 新增文件保存历史记录
        if (Objects.equals(EnumConstant.Node_Type_1, jsCodeFile.getNodeType())) {
            addHistory(jsCodeFile);
        }
        jsCodeFile = jsCodeFileMapper.selectById(jsCodeFile.getId());
        // 发布新增事件
        applicationContext.publishEvent(new JsCodeFileChangeEvent(this, JsCodeFileChangeEnum.Add, jsCodeFile));
        return jsCodeFile;
    }

    @Transactional
    public JsCodeFile updateJsCodeFile(Long id, JsCodeFileUpdateReq req) {
        JsCodeFile old = jsCodeFileMapper.selectById(id);
        if (old == null) {
            throw new BusinessException("更新数据不存在");
        }
        if (Objects.equals(old.getReadOnly(), EnumConstant.Read_Only_1)) {
            throw new BusinessException("当前文件只读，不允许修改");
        }
        // 文件夹没有脚本内容
        if (Objects.equals(EnumConstant.Node_Type_2, old.getNodeType())) {
            req.setJsCode(null);
            if (req.getName() != null && req.getName().endsWith(".js")) {
                throw new BusinessException("文件夹名称不能以“.js”结尾");
            }
        } else if (req.getName() != null && !req.getName().endsWith(".js")) {
            req.setName(StringUtils.trim(req.getName()) + ".js");
        }
        // 更新filePath 或者 更新name
        boolean updateFullPath = false;
        // 如果更新 filePath 则校验
        if (req.getFilePath() != null
                && !Objects.equals(req.getFilePath(), old.getFilePath())
                && !Objects.equals(req.getFilePath(), EnumConstant.File_Path_Separator)) {
            TupleTow<String, String> tupleTow = getParentPath(req.getFilePath());
            JsCodeFile parent = jsCodeFileMapper.getJsCodeFile(old.getBizType(), old.getGroupName(), EnumConstant.Node_Type_2, tupleTow.getValue1(), tupleTow.getValue2());
            if (parent == null) {
                throw new BusinessException("父路径不存在");
            }
            // 文件夹更新filePath
            updateFullPath = true;
        }
        // 如果更新 name 则校验
        if (req.getName() != null && !Objects.equals(req.getName(), old.getName())) {
            String filePath = req.getFilePath() != null ? req.getFilePath() : old.getFilePath();
            JsCodeFile exists = jsCodeFileMapper.getByFullPath(old.getBizType(), old.getGroupName(), filePath, req.getName());
            if (exists != null) {
                throw new BusinessException("与已经存在的文件" + (Objects.equals(EnumConstant.Node_Type_2, exists.getNodeType()) ? "夹" : "") + "同名，请重新指定名称");
            }
            // 文件夹更新name
            updateFullPath = true;
        }
        // 记录历史记录
        if (Objects.equals(EnumConstant.Node_Type_1, old.getNodeType())) {
            addHistory(old);
        }
        // 更新数据
        JsCodeFile update = BeanMapper.mapper(req, JsCodeFile.class);
        update.setId(old.getId());
        jsCodeFileMapper.updateById(update);
        update = jsCodeFileMapper.selectById(id);
        // 文件夹更新filePath 或者 文件夹更新name
        if (updateFullPath && Objects.equals(EnumConstant.Node_Type_2, old.getNodeType())) {
            String oldFullPath = JsCodeFilePathUtils.concat(old.getFilePath(), old.getName());
            String newFullPath = JsCodeFilePathUtils.concat(update.getFilePath(), update.getName());
            List<JsCodeFile> childList = jsCodeFileMapper.findAllChildByFilePath(oldFullPath);
            List<JsCodeFile> updateList = new ArrayList<>(childList.size());
            childList.forEach((file) -> {
                String filePath = file.getFilePath().substring(oldFullPath.length());
                filePath = newFullPath + filePath;
                JsCodeFile tmp = new JsCodeFile();
                tmp.setId(file.getId());
                tmp.setFilePath(filePath);
                updateList.add(tmp);
            });
            updateList.forEach(file -> jsCodeFileMapper.updateById(file));
            childList = jsCodeFileMapper.selectBatchIds(updateList.stream().map(JsCodeFile::getId).collect(Collectors.toSet()));
            childList.forEach(file -> applicationContext.publishEvent(new JsCodeFileChangeEvent(this, JsCodeFileChangeEnum.Update, file)));
        }
        // 发布更新事件
        applicationContext.publishEvent(new JsCodeFileChangeEvent(this, JsCodeFileChangeEnum.Update, update));
        return update;
    }

    @Transactional
    public JsCodeFile deleteJsCodeFile(Long id) {
        JsCodeFile old = jsCodeFileMapper.selectById(id);
        if (old == null) {
            return null;
        }
        if (Objects.equals(old.getDisableDelete(), EnumConstant.Disable_Delete_1)) {
            throw new BusinessException("不可删除当前数据，请联系管理员");
        }
        if (Objects.equals(old.getNodeType(), EnumConstant.Node_Type_1)) {
            // 删除文件
            jsCodeFileMapper.deleteById(id);
        } else if (Objects.equals(old.getNodeType(), EnumConstant.Node_Type_2)) {
            // 查询所有需要删除的子目录或者文件
            List<JsCodeFile> childList = jsCodeFileMapper.findAllChildByFilePath(JsCodeFilePathUtils.concat(old.getFilePath(), old.getName()));
            List<Long> idList = new ArrayList<>(childList.size() + 1);
            idList.add(old.getId());
            childList.forEach(file -> idList.add(file.getId()));
            // 执行删除
            int count = jsCodeFileMapper.deleteBatchIds(idList);
            if (count > 0) {
                applicationContext.publishEvent(new JsCodeFileChangeEvent(this, JsCodeFileChangeEnum.Delete, old));
                childList.forEach(file -> applicationContext.publishEvent(new JsCodeFileChangeEvent(this, JsCodeFileChangeEnum.Delete, file)));
            }
        }
        return old;
    }

    public Map<String, List<String>> allBizType() {
        Map<String, List<String>> result = new HashMap<>();
        List<Map<String, String>> mapList = jsCodeFileMapper.allBizType();
        mapList.forEach(map -> {
            // biz_type, group_name
            String bizType = map.get("biz_type");
            String groupName = map.get("group_name");
            List<String> list = result.computeIfAbsent(bizType, s -> new ArrayList<>());
            list.add(groupName);
        });
        return result;
    }

    public List<String> bizTypeList() {
        return jsCodeFileMapper.bizTypeList();
    }

    public List<String> allGroupName(String bizType) {
        return jsCodeFileMapper.allGroupName(bizType);
    }

    @Transactional
    public JsCodeFile lockFile(Long id) {
        JsCodeFile old = jsCodeFileMapper.selectById(id);
        if (old == null) {
            throw new BusinessException("文件不存在");
        }
        JsCodeFile update = new JsCodeFile();
        update.setId(old.getId());
        update.setReadOnly(EnumConstant.Read_Only_1);
        jsCodeFileMapper.updateById(update);
        return jsCodeFileMapper.selectById(old.getId());
    }

    @Transactional
    protected void addHistory(JsCodeFile jsCodeFile) {
        CodeFileHistory old = codeFileHistoryMapper.getLastHistory(jsCodeFile.getBizType(), jsCodeFile.getGroupName(), jsCodeFile.getFilePath(), jsCodeFile.getName());
        if (old != null && Objects.equals(StringUtils.trim(jsCodeFile.getJsCode()), StringUtils.trim(old.getJsCode()))) {
            return;
        }
        CodeFileHistory codeFileHistory = new CodeFileHistory();
        codeFileHistory.setBizType(jsCodeFile.getBizType());
        codeFileHistory.setGroupName(jsCodeFile.getGroupName());
        codeFileHistory.setFilePath(jsCodeFile.getFilePath());
        codeFileHistory.setName(jsCodeFile.getName());
        codeFileHistory.setJsCode(jsCodeFile.getJsCode());
        codeFileHistory.setDescription(jsCodeFile.getDescription());
        codeFileHistoryMapper.insert(codeFileHistory);
    }
}
