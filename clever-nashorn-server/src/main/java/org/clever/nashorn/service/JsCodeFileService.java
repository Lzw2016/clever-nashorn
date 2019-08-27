package org.clever.nashorn.service;

import org.clever.nashorn.cache.JsCodeFileCache;
import org.clever.nashorn.mapper.CodeFileHistoryMapper;
import org.clever.nashorn.mapper.JsCodeFileMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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


}
