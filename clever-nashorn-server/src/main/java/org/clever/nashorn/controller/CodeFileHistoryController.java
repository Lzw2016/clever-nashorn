package org.clever.nashorn.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.clever.nashorn.dto.request.CodeFileHistoryQueryReq;
import org.clever.nashorn.dto.request.FileNameHistoryReq;
import org.clever.nashorn.dto.request.RevertFileReq;
import org.clever.nashorn.entity.CodeFileHistory;
import org.clever.nashorn.entity.JsCodeFile;
import org.clever.nashorn.service.CodeFileHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    @ApiOperation("根据id获取")
    @GetMapping("/{id}")
    public CodeFileHistory getCodeFileHistory(@PathVariable("id") Long id) {
        return codeFileHistoryService.getCodeFileHistory(id);
    }

    @ApiOperation("根据业务类型，分组名称，全路径查询列表")
    @GetMapping("/query")
    public IPage<CodeFileHistory> queryByPage(@Validated CodeFileHistoryQueryReq query) {
        return codeFileHistoryService.queryByPage(query);
    }

    @ApiOperation("根据业务类型，分组名称，父级路径查询所有父路径下的历史文件名")
    @GetMapping("/file_name")
    public List<String> fileName(@Validated FileNameHistoryReq query) {
        return codeFileHistoryService.fileName(query);
    }

    @ApiOperation("退回代码到某个版本")
    @GetMapping("/revert_file")
    public JsCodeFile revertFile(@Validated RevertFileReq req) {
        return codeFileHistoryService.revertFile(req);
    }
}
