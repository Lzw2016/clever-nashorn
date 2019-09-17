package org.clever.nashorn.service;

import org.apache.commons.lang3.StringUtils;
import org.clever.common.exception.BusinessException;
import org.clever.common.utils.mapper.BeanMapper;
import org.clever.common.utils.tree.BuildTreeUtils;
import org.clever.common.utils.tuples.TupleTow;
import org.clever.nashorn.cache.JsCodeFileCache;
import org.clever.nashorn.dto.request.JsCodeFileAddReq;
import org.clever.nashorn.dto.request.JsCodeFileTreeFindReq;
import org.clever.nashorn.dto.request.JsCodeFileUpdateReq;
import org.clever.nashorn.entity.CodeFileHistory;
import org.clever.nashorn.entity.EnumConstant;
import org.clever.nashorn.entity.JsCodeFile;
import org.clever.nashorn.mapper.CodeFileHistoryMapper;
import org.clever.nashorn.mapper.JsCodeFileMapper;
import org.clever.nashorn.model.JsCodeFileNode;
import org.clever.nashorn.utils.JsCodeFilePathUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    private JsCodeFileCache jsCodeFileCache;

    public JsCodeFile getJsCodeFile(Long id) {
        JsCodeFile jsCodeFile = jsCodeFileMapper.selectById(id);
        if (jsCodeFile != null) {
            jsCodeFileCache.put(jsCodeFile);
        }
        return jsCodeFile;
    }

    public List<JsCodeFileNode> findJsCodeFileTree(JsCodeFileTreeFindReq req) {
        final List<JsCodeFileNode> treeList = new ArrayList<>();
        List<JsCodeFile> list = jsCodeFileMapper.findByBizAndGroup(req.getBizType(), req.getGroupName());
        if (list.size() <= 0) {
            return treeList;
        }
//        // 增加根路径
//        JsCodeFile root = new JsCodeFile();
//        root.setId(-1L);
//        root.setBizType(req.getBizType());
//        root.setGroupName(req.getGroupName());
//        root.setNodeType(EnumConstant.Node_Type_2);
//        root.setFilePath("");
//        root.setName(EnumConstant.File_Path_Separator);
//        treeList.add(new JsCodeFileNode(root));
        // 增加子路径
        list.forEach(jsCodeFile -> treeList.add(new JsCodeFileNode(jsCodeFile)));
        return BuildTreeUtils.buildTree(treeList);
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
            TupleTow<String, String> tupleTow = JsCodeFilePathUtils.getParentPath(req.getFilePath());
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
        return jsCodeFile;
    }

    @Transactional
    public JsCodeFile updateJsCodeFile(Long id, JsCodeFileUpdateReq req) {
        JsCodeFile old = jsCodeFileMapper.selectById(id);
        if (old == null) {
            throw new BusinessException("更新数据不存在");
        }
        // 文件夹没有脚本内容
        if (Objects.equals(EnumConstant.Node_Type_2, old.getNodeType())) {
            req.setJsCode(null);
        }
        boolean addHistory = false;
        // 如果更新 filePath 则校验
        if (req.getFilePath() != null
                && !Objects.equals(req.getFilePath(), old.getFilePath())
                && !Objects.equals(req.getFilePath(), EnumConstant.File_Path_Separator)) {
            addHistory = true;
            TupleTow<String, String> tupleTow = JsCodeFilePathUtils.getParentPath(req.getFilePath());
            JsCodeFile parent = jsCodeFileMapper.getJsCodeFile(old.getBizType(), old.getGroupName(), EnumConstant.Node_Type_2, tupleTow.getValue1(), tupleTow.getValue2());
            if (parent == null) {
                throw new BusinessException("父路径不存在");
            }
            // TODO 文件夹更新filePath??
        }
        // 如果更新 name 则校验
        if (req.getName() != null && !Objects.equals(req.getName(), old.getName())) {
            addHistory = true;
            String filePath = req.getFilePath() != null ? req.getFilePath() : old.getFilePath();
            JsCodeFile exists = jsCodeFileMapper.getByFullPath(old.getBizType(), old.getGroupName(), filePath, req.getName());
            if (exists != null) {
                throw new BusinessException("与已经存在的文件" + (Objects.equals(EnumConstant.Node_Type_2, exists.getNodeType()) ? "夹" : "") + "同名，请重新指定名称");
            }
            // TODO 文件夹更新name??
        }
        // 记录历史记录
        if (Objects.equals(EnumConstant.Node_Type_1, old.getNodeType()) && (!Objects.equals(StringUtils.trim(req.getJsCode()), StringUtils.trim(old.getJsCode())) || addHistory)) {
            addHistory(old);
        }
        // 更新数据
        JsCodeFile update = BeanMapper.mapper(req, JsCodeFile.class);
        update.setId(old.getId());
        jsCodeFileMapper.updateById(update);
        return jsCodeFileMapper.selectById(id);
    }

    @Transactional
    protected void addHistory(JsCodeFile jsCodeFile) {
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
