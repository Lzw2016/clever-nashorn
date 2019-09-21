package org.clever.nashorn.cache;

import org.clever.nashorn.entity.JsCodeFile;
import org.clever.nashorn.mapper.JsCodeFileMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * 作者： lzw<br/>
 * 创建时间：2019-09-21 10:34 <br/>
 */
@Transactional(readOnly = true)
@Service
public class JsCodeFileCacheService {
    @Autowired
    private JsCodeFileMapper jsCodeFileMapper;

    public JsCodeFile getJsCodeFile(String bizType, String groupName, int nodeType, String filePath, String name) {
        return jsCodeFileMapper.getJsCodeFile(bizType, groupName, nodeType, filePath, name);
    }

    public Set<JsCodeFile> findAll() {
        return jsCodeFileMapper.findAll();
    }
}
