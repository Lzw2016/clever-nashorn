package org.clever.nashorn.service;

import org.clever.nashorn.mapper.CodeRunLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 作者：lizw <br/>
 * 创建时间：2019/08/27 09:34 <br/>
 */
@Transactional(readOnly = true)
@Service
public class CodeRunLogService {
    @Autowired
    private CodeRunLogMapper codeRunLogMapper;

}
