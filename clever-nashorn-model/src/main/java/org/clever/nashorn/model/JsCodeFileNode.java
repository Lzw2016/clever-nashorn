package org.clever.nashorn.model;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Data;
import org.clever.common.utils.tree.ITreeNode;
import org.clever.nashorn.entity.EnumConstant;
import org.clever.nashorn.entity.JsCodeFile;
import org.clever.nashorn.utils.JsCodeFilePathUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 作者：lizw <br/>
 * 创建时间：2019/08/27 14:13 <br/>
 */
@Data
public class JsCodeFileNode implements ITreeNode {

    @JsonUnwrapped
    private JsCodeFile jsCodeFile;
    /**
     * 数据库数据ID
     */
    private Long dataId;

    // ----------------------------------------------------------------------------------
    /**
     * 是否被添加到父节点下
     */
    private boolean isBuild = false;
    /**
     * 子节点
     */
    private List<ITreeNode> children;

    public JsCodeFileNode(JsCodeFile jsCodeFile) {
        this.jsCodeFile = jsCodeFile;
        this.dataId = jsCodeFile.getId();
    }

    /**
     * 得到当前全路径，文件夹不以“/”号结尾
     */
    public String getFullPath() {
        return JsCodeFilePathUtils.concat(jsCodeFile.getFilePath(), jsCodeFile.getName());
    }

    /**
     * 全路径，文件夹以“/”号结尾
     */
    @Override
    public Object getId() {
        String id = JsCodeFilePathUtils.concat(jsCodeFile.getFilePath(), jsCodeFile.getName());
        if (Objects.equals(EnumConstant.Node_Type_2, jsCodeFile.getNodeType())) {
            id = JsCodeFilePathUtils.getFilePath(id);
        }
        return id;
    }

    /**
     * 上级路径，以“/”号结尾
     */
    @Override
    public Object getParentId() {
        return jsCodeFile.getFilePath();
    }

    @Override
    public boolean isBuild() {
        return isBuild;
    }

    @Override
    public void setBuild(boolean isBuild) {
        this.isBuild = isBuild;
    }

    @Override
    public List<ITreeNode> getChildren() {
        return children;
    }

    @Override
    public void addChildren(ITreeNode node) {
        if (this.children == null) {
            this.children = new ArrayList<>();
        }
        this.children.add(node);
    }
}
