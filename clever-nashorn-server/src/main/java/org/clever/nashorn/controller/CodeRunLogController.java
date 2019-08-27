package org.clever.nashorn.controller;

import io.swagger.annotations.Api;
import org.clever.nashorn.service.CodeRunLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 作者：lizw <br/>
 * 创建时间：2019/08/27 09:44 <br/>
 */
@Api("JS代码运行日志")
@RequestMapping("/api/code_run_log")
@RestController
public class CodeRunLogController {
    @Autowired
    private CodeRunLogService codeRunLogService;

}
