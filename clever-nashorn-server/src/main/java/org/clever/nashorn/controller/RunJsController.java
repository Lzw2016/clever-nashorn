package org.clever.nashorn.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.apache.commons.lang3.StringUtils;
import org.clever.common.exception.BusinessException;
import org.clever.common.model.response.AjaxMessage;
import org.clever.common.utils.DateTimeUtils;
import org.clever.common.utils.IDCreateUtils;
import org.clever.common.utils.exception.ExceptionUtils;
import org.clever.common.utils.spring.SpringContextHolder;
import org.clever.nashorn.ScriptModuleInstance;
import org.clever.nashorn.cache.MemoryJsCodeFileCache;
import org.clever.nashorn.dto.request.DebugReq;
import org.clever.nashorn.entity.CodeRunLog;
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
import org.clever.nashorn.utils.StrFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

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
    private static final ExecutorService EXECUTOR_SERVICE = new ThreadPoolExecutor(32, 32, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>());

    private static final Map<String, Long> Task_Mapping = new HashMap<>(1000);

    private static final ConcurrentHashMap<Long, ScriptObjectMirror> Task_Log = new ConcurrentHashMap<>();

    @Autowired
    private CodeRunLogService codeRunLogService;
    @Autowired
    private JsCodeFileService jsCodeFileService;

    @ApiOperation("后台运行脚本")
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
        String taskId = String.format("%s-%s", note, IDCreateUtils.uuid());
        EXECUTOR_SERVICE.execute(() -> {
            ScriptObjectMirror logsArray = null;
            String stackTrace = "";
            Long codeRunLogId = codeRunLogService.startLog(jsCodeFile);
            Task_Mapping.put(taskId, codeRunLogId);
            Integer status = EnumConstant.Run_Log_Status_2;
            try {
                ScriptObjectMirror scriptObjectMirror = scriptModuleInstance.useJs(fileFullPath);
                Object logs = scriptObjectMirror.get("logs");
                if (logs instanceof ScriptObjectMirror) {
                    logsArray = (ScriptObjectMirror) logs;
                    Task_Log.put(codeRunLogId, logsArray);
                }
                scriptObjectMirror.callMember(fucName);
                // codeRunLogService.appendLog();
            } catch (Exception e) {
                status = EnumConstant.Run_Log_Status_3;
                stackTrace = ExceptionUtils.getStackTraceAsString(e);
            } finally {
                codeRunLogService.appendLog(codeRunLogId, String.format("%s\n%s", getLogs(logsArray), stackTrace));
                codeRunLogService.endLog(codeRunLogId, status);
                Task_Log.remove(codeRunLogId);
            }
        });
        return new AjaxMessage<>(taskId, "任务启动成功");
    }

    @ApiOperation("查询脚本日志")
    @GetMapping("/logs")
    public void logs(@RequestParam("taskId") String taskId, HttpServletResponse response) throws IOException {
        Long codeRunLogId = Task_Mapping.get(taskId);
        if (codeRunLogId == null) {
            throw new BusinessException("任务不存在");
        }
        CodeRunLog codeRunLog = codeRunLogService.getCodeRunLog(codeRunLogId);
        if (codeRunLog != null && StringUtils.isNotBlank(codeRunLog.getRunLog())) {
            response.setContentType("text/plain");
            response.getWriter().append(getLogsSummary(codeRunLog)).append("\n\n");
            response.getWriter().append(codeRunLog.getRunLog()).flush();
            return;
        }
        ScriptObjectMirror logsArray = Task_Log.get(codeRunLogId);
        if (logsArray == null) {
            throw new BusinessException("任务不存在");
        }
        response.setContentType("text/plain");
        if (codeRunLog != null) {
            response.getWriter().append(getLogsSummary(codeRunLog)).append("\n\n");
        }
        response.getWriter().append(getLogs(logsArray)).flush();
    }

    @ApiOperation("正在运行的taskId")
    @GetMapping("/run_task_id")
    public Map<String, Long> runTask() {
        Map<String, Long> task = new HashMap<>();
        Task_Log.keySet().forEach(aLong -> {
            String taskId = null;
            for (Map.Entry<String, Long> entry : Task_Mapping.entrySet()) {
                if (entry.getValue().equals(aLong)) {
                    taskId = entry.getKey();
                    break;
                }
            }
            if (taskId != null) {
                task.put(taskId, aLong);
            }
        });
        return task;
    }

    @ApiOperation("所有的的taskId")
    @GetMapping("/all_task_id")
    public Map<String, Long> allTask() {
        return Task_Mapping;
    }

    private String getLogsSummary(CodeRunLog codeRunLog) {
        String line = "==========================================================================================================================================================================";
        StringBuilder sb = new StringBuilder();
        sb.append("#").append(line).append("\n");
        sb.append("# 运行状态： ").append(codeRunLog.getStatus()).append(" (1-运行中，2-成功，3-异常，4-超时)").append("\n");
        sb.append("# 运行时间： ")
                .append(DateTimeUtils.formatToString(codeRunLog.getRunStart()))
                .append(" -- ")
                .append(codeRunLog.getRunEnd() == null ? "?" : DateTimeUtils.formatToString(codeRunLog.getRunEnd()))
                .append("\n");
        Date runEnd = codeRunLog.getRunEnd();
        if (runEnd == null) {
            runEnd = new Date();
        }
        sb.append("# 运行耗时： ").append(runEnd.getTime() - codeRunLog.getRunStart().getTime()).append("ms").append("\n");
        sb.append("#").append(line).append("\n");
        return sb.toString();
    }

    private String getLogs(ScriptObjectMirror logsArray) {
        if (logsArray == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> entry : logsArray.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            Object[] array = null;
            if (value instanceof ScriptObjectMirror && ((ScriptObjectMirror) value).isArray()) {
                array = ((ScriptObjectMirror) value).values().toArray();
            }
            sb.append(key).append("\t");
            if (array != null) {
                sb.append(logString(array));
            } else {
                sb.append(logString(value));
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * 根据日志输出参数得到日志字符串
     */
    private String logString(Object... args) {
        if (args == null || args.length <= 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder(args.length * 32);
        for (Object arg : args) {
            String str = StrFormatter.toString(arg);
            sb.append(str);
        }
        return sb.toString();
    }
}
