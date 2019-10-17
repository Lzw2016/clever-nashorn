package org.clever.nashorn.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.clever.nashorn.dto.request.JsCodeFileAddReq;
import org.clever.nashorn.dto.request.JsCodeFileTreeFindReq;
import org.clever.nashorn.dto.request.JsCodeFileUpdateReq;
import org.clever.nashorn.entity.JsCodeFile;
import org.clever.nashorn.model.JsCodeFileNode;
import org.clever.nashorn.service.JsCodeFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    @ApiOperation("新增(文件或者文件夹)")
    @PostMapping("/add")
    public JsCodeFile addJsCodeFile(@RequestBody @Validated JsCodeFileAddReq req) {
        return jsCodeFileService.addJsCodeFile(req);
    }

    @ApiOperation("更新(文件或者文件夹)")
    @PutMapping("/{id}")
    public JsCodeFile updateJsCodeFile(@PathVariable("id") Long id, @RequestBody @Validated JsCodeFileUpdateReq req) {
        return jsCodeFileService.updateJsCodeFile(id, req);
    }

    @ApiOperation("删除文件或者文件夹")
    @DeleteMapping("/{id}")
    public JsCodeFile deleteJsCodeFile(@PathVariable("id") Long id) {
        return jsCodeFileService.deleteJsCodeFile(id);
    }

    @ApiOperation("查询所有的业务类型和分组名称并返回关联关系")
    @GetMapping("/all_biz_type")
    public Map<String, List<String>> allBizType() {
        return jsCodeFileService.allBizType();
    }

    @ApiOperation("所有业务类型")
    @GetMapping("/biz_type")
    public List<String> bizType() {
        return jsCodeFileService.bizTypeList();
    }

    @ApiOperation("根据业务类型查询所有分组名称")
    @GetMapping("/group_name")
    public List<String> allGroupName(@RequestParam("bizType") String bizType) {
        return jsCodeFileService.allGroupName(bizType);
    }

    @ApiOperation("锁住文件(不允许修改)")
    @PutMapping("/lock_file/{id}")
    public JsCodeFile lockFile(@PathVariable("id") Long id) {
        return jsCodeFileService.lockFile(id);
    }
}
