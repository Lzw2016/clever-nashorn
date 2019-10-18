package org.clever.nashorn.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.clever.nashorn.dto.request.CodeRunLogQueryReq;
import org.clever.nashorn.dto.response.CodeRunLogQueryRes;
import org.clever.nashorn.dto.response.CodeRunLogStatusSummaryRes;
import org.clever.nashorn.entity.CodeRunLog;
import org.clever.nashorn.service.CodeRunLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    @ApiOperation("根据id获取")
    @GetMapping("/{id}")
    public CodeRunLog getCodeRunLog(@PathVariable("id") Long id) {
        return codeRunLogService.getCodeRunLog(id);
    }

    @ApiOperation("业务类型，分组名称，文件ID，全路径，运行时间，运行状态，组合条件查询脚本运行日志")
    @GetMapping("/query")
    public IPage<CodeRunLogQueryRes> queryByPage(@Validated CodeRunLogQueryReq query) {
        return codeRunLogService.queryByPage(query);
    }

    @ApiOperation("运行状态摘要信息")
    @GetMapping("/summary")
    public List<CodeRunLogStatusSummaryRes> groupByStatus(@Validated CodeRunLogQueryReq query) {
        return codeRunLogService.groupByStatus(query);
    }
}
