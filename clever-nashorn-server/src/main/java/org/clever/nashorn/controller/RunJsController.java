package org.clever.nashorn.controller;

import io.swagger.annotations.Api;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.clever.common.exception.BusinessException;
import org.clever.common.model.response.AjaxMessage;
import org.clever.common.utils.IDCreateUtils;
import org.clever.common.utils.exception.ExceptionUtils;
import org.clever.common.utils.spring.SpringContextHolder;
import org.clever.nashorn.ScriptModuleInstance;
import org.clever.nashorn.cache.MemoryJsCodeFileCache;
import org.clever.nashorn.dto.request.DebugReq;
import org.clever.nashorn.entity.EnumConstant;
import org.clever.nashorn.entity.JsCodeFile;
import org.clever.nashorn.folder.DatabaseFolder;
import org.clever.nashorn.folder.Folder;
import org.clever.nashorn.internal.Console;
import org.clever.nashorn.internal.LogConsole;
import org.clever.nashorn.module.cache.MemoryModuleCache;
import org.clever.nashorn.module.cache.ModuleCache;
import org.clever.nashorn.service.CodeRunLogService;
import org.clever.nashorn.service.JsCodeFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 作者：lizw <br/>
 * 创建时间：2020/04/16 16:05 <br/>
 */
@Api("系统任务")
@RequestMapping("/api/run_js")
@RestController
public class RunJsController {
    /**
     * 所有的任务 Map<taskId, ThreadPoolExecutor> <br />
     */
    private static final ConcurrentHashMap<String, ExecutorService> Thread_Pool_Map = new ConcurrentHashMap<>();

    @Autowired
    private CodeRunLogService codeRunLogService;
    @Autowired
    private JsCodeFileService jsCodeFileService;

    @GetMapping("/run")
    public AjaxMessage<String> run(
            @RequestParam("fileFullPath") String fileFullPath,
            @RequestParam("fucName") String fucName,
            @RequestParam("note") String note) {
        DebugReq req = new DebugReq("default", "default");
        req.setFileFullPath(fileFullPath);
        req.setFucName(fucName);

        JsCodeFile jsCodeFile = jsCodeFileService.getFile(req.getBizType(), req.getGroupName(), req.getFileFullPath());
        if (jsCodeFile == null) {
            throw new BusinessException("文件不存在");
        }

        MemoryJsCodeFileCache jsCodeFileCache = new MemoryJsCodeFileCache();
        Folder rootFolder = new DatabaseFolder(req.getBizType(), req.getGroupName(), jsCodeFileCache);
        ModuleCache moduleCache = new MemoryModuleCache();
        Console console = new LogConsole(req.getBizType(), req.getGroupName(), req.getFileFullPath());
        Map<String, Object> context = SpringContextHolder.getBean("ScriptGlobalContext");
        ScriptModuleInstance scriptModuleInstance = new ScriptModuleInstance(rootFolder, moduleCache, console, context);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            String stackTrace = null;
            Long logId = codeRunLogService.startLog(jsCodeFile);
            Integer status = EnumConstant.Run_Log_Status_2;
            try {
                ScriptObjectMirror scriptObjectMirror = scriptModuleInstance.useJs(fileFullPath);
                scriptObjectMirror.callMember(fucName);
                Object object = scriptObjectMirror.get("logs");
                // codeRunLogService.appendLog();
            } catch (Exception e) {
                status = EnumConstant.Run_Log_Status_3;
                stackTrace = ExceptionUtils.getStackTraceAsString(e);
            } finally {
                codeRunLogService.endLog(logId, status);
            }
        });

        String taskId = String.format("%s-%s", note, IDCreateUtils.uuid());
        Thread_Pool_Map.put(taskId, executor);
        return new AjaxMessage<>(taskId, "任务启动成功");
    }
}
