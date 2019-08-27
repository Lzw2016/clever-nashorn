package org.clever.nashorn.model;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Data;
import org.clever.common.utils.tree.ITreeNode;
import org.clever.nashorn.entity.JsCodeFile;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：lizw <br/>
 * 创建时间：2019/08/27 14:13 <br/>
 */
@Data
public class JsCodeFileNode implements ITreeNode {

    @JsonUnwrapped
    private JsCodeFile jsCodeFile;

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
    }

    @Override
    public Object getId() {
        return jsCodeFile.getFilePath() + jsCodeFile.getName();
    }

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
