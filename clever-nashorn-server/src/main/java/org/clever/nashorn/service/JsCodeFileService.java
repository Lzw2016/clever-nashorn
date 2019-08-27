package org.clever.nashorn.service;

import org.clever.common.exception.BusinessException;
import org.clever.common.utils.mapper.BeanMapper;
import org.clever.common.utils.tree.BuildTreeUtils;
import org.clever.nashorn.cache.JsCodeFileCache;
import org.clever.nashorn.dto.request.JsCodeFileAddReq;
import org.clever.nashorn.dto.request.JsCodeFileTreeFindReq;
import org.clever.nashorn.entity.EnumConstant;
import org.clever.nashorn.entity.JsCodeFile;
import org.clever.nashorn.mapper.CodeFileHistoryMapper;
import org.clever.nashorn.mapper.JsCodeFileMapper;
import org.clever.nashorn.model.JsCodeFileNode;
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
        list.forEach(jsCodeFile -> {
            treeList.add(new JsCodeFileNode(jsCodeFile));
            jsCodeFileCache.put(jsCodeFile);
        });
        return BuildTreeUtils.buildTree(treeList);
    }

    @Transactional
    public JsCodeFile addJsCodeFile(JsCodeFileAddReq req) {
        JsCodeFile jsCodeFile = BeanMapper.mapper(req, JsCodeFile.class);
        // 保证根路径存在
        JsCodeFile root = jsCodeFileMapper.getJsCodeFile(req.getBizType(), req.getGroupName(), EnumConstant.Node_Type_2, EnumConstant.File_Path_Separator);
        if (root == null) {
            root = new JsCodeFile();
            root.setBizType(req.getBizType());
            root.setGroupName(req.getGroupName());
            root.setNodeType(EnumConstant.Node_Type_2);
            root.setFilePath("");
            root.setName(EnumConstant.File_Path_Separator);
            jsCodeFileMapper.insert(root);
        }
        // 文件夹的name是以“/”号结尾
        if (Objects.equals(EnumConstant.Node_Type_2, jsCodeFile.getNodeType())) {
            // jsCodeFile.setName(jsCodeFile.getName() + EnumConstant.File_Path_Separator);
            jsCodeFile.setJsCode(null);
        }
        // 校验父路径存在
        JsCodeFile parent = jsCodeFileMapper.getJsCodeFile(req.getBizType(), req.getGroupName(), EnumConstant.Node_Type_2, req.getFilePath());
        if (parent == null) {
            throw new BusinessException("父路径不存在");
        }
        // 校验文件路径不重复
        JsCodeFile exists = jsCodeFileMapper.getByFullPath(req.getBizType(), req.getGroupName(), req.getFilePath() + req.getName());
        if (exists != null) {
            throw new BusinessException("与存在的文件" + (Objects.equals(EnumConstant.Node_Type_2, exists.getNodeType()) ? "夹" : "") + "同名，请重新指定名称");
        }
        // 保存数据
        jsCodeFileMapper.insert(jsCodeFile);
        return jsCodeFile;
    }
}
