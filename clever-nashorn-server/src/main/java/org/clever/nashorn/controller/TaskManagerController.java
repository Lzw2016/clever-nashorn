package org.clever.nashorn.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.clever.nashorn.dto.response.TaskManagerRes;
import org.clever.nashorn.websocket.Handler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 作者：lizw <br/>
 * 创建时间：2019/10/18 10:10 <br/>
 */
@Api("系统任务")
@RequestMapping("/api/task")
@RestController
public class TaskManagerController {

    @ApiOperation("任务摘要信息")
    @GetMapping("/summary_info")
    public TaskManagerRes getTaskManager() {
        return Handler.clearTask(true);
    }

    @ApiOperation("停止任务")
    @GetMapping("/stop_task")
    public TaskManagerRes stopTask(@RequestParam("taskId") String taskId) throws InterruptedException {
        return Handler.stopTask(taskId);
    }
}
