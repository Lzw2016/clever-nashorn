package org.clever.nashorn.controller;

import io.swagger.annotations.Api;
import org.clever.nashorn.service.CodeFileHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 作者：lizw <br/>
 * 创建时间：2019/08/27 09:41 <br/>
 */
@Api("历史JS代码")
@RequestMapping("/api/code_file_history")
@RestController
public class CodeFileHistoryController {
    @Autowired
    private CodeFileHistoryService codeFileHistoryService;



}
