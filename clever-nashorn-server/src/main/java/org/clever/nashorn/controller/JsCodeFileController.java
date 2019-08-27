package org.clever.nashorn.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.clever.nashorn.dto.request.JsCodeFileAddReq;
import org.clever.nashorn.dto.request.JsCodeFileTreeFindReq;
import org.clever.nashorn.entity.JsCodeFile;
import org.clever.nashorn.model.JsCodeFileNode;
import org.clever.nashorn.service.JsCodeFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @ApiOperation("根据id获取")
    @GetMapping("/{id}")
    public JsCodeFile getJsCodeFile(@PathVariable("id") Long id) {
        return jsCodeFileService.getJsCodeFile(id);
    }

    @ApiOperation("获取代码Tree")
    @GetMapping("/tree")
    public List<JsCodeFileNode> findJsCodeFileTree(@Validated JsCodeFileTreeFindReq req) {
        return jsCodeFileService.findJsCodeFileTree(req);
    }

    @ApiOperation("新增")
    @PostMapping("/add")
    public JsCodeFile addJsCodeFile(@RequestBody @Validated JsCodeFileAddReq req) {
        return jsCodeFileService.addJsCodeFile(req);
    }
}
