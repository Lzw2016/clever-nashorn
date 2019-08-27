package org.clever.nashorn.controller;

import io.swagger.annotations.Api;
import org.clever.nashorn.service.JsCodeFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 作者：lizw <br/>
 * 创建时间：2019/08/27 09:47 <br/>
 */
@Api("系统JS代码")
@RequestMapping("/api/js_code_file")
@RestController
public class JsCodeFileController {
    @Autowired
    private JsCodeFileService jsCodeFileService;


}
