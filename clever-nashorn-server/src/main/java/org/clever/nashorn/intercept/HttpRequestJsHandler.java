package org.clever.nashorn.intercept;

import com.fasterxml.jackson.databind.ObjectMapper;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import jdk.nashorn.internal.runtime.Undefined;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.clever.common.utils.mapper.JacksonMapper;
import org.clever.common.utils.tuples.TupleFive;
import org.clever.common.utils.tuples.TupleTow;
import org.clever.nashorn.ScriptModuleInstance;
import org.clever.nashorn.cache.JsCodeFileCache;
import org.clever.nashorn.entity.JsCodeFile;
import org.clever.nashorn.utils.JsCodeFilePathUtils;
import org.clever.nashorn.utils.ScriptEngineUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * 作者：lizw <br/>
 * 创建时间：2019/09/20 16:21 <br/>
 */
@Slf4j
public class HttpRequestJsHandler implements HandlerInterceptor {
    /**
     * 请求支持的后缀，建议使用特殊的后缀表示使用动态js代码处理请求
     */
    private static final Set<String> Support_Suffix = new HashSet<String>() {{
        add("");
        add(".json");
        add(".action");
    }};
    /**
     * JS处理请求的方法名
     */
    private static final String Handler_Method = "service";
    /**
     * 处理请求脚本的文件名称
     */
    private static final String Handler_File_Name = "controller.js";

    /**
     * 业务类型
     */
    @Getter
    private final String bizType;
    /**
     * 代码分组
     */
    @Getter
    private final String groupName;
    /**
     * 响应数据序列化
     */
    private final JacksonMapper jacksonMapper;
    /**
     * 脚本缓存
     */
    @Getter
    private final JsCodeFileCache jsCodeFileCache;
    /**
     * js引擎模块实例
     */
    @Getter
    private final ScriptModuleInstance scriptModuleInstance;

    public HttpRequestJsHandler(final String bizType, final String groupName, ObjectMapper objectMapper, JsCodeFileCache jsCodeFileCache, ScriptModuleInstance scriptModuleInstance) {
        this.bizType = bizType;
        this.groupName = groupName;
        jacksonMapper = new JacksonMapper(objectMapper);
        this.jsCodeFileCache = jsCodeFileCache;
        this.scriptModuleInstance = scriptModuleInstance;
    }

    private boolean jsCodeFileExists(String fileFullName) {
        TupleTow<String, String> tupleTow = JsCodeFilePathUtils.getParentPath(fileFullName);
        JsCodeFile jsCodeFile = jsCodeFileCache.getFile(bizType, groupName, tupleTow.getValue1(), tupleTow.getValue2());
        return jsCodeFile != null && StringUtils.isNotBlank(jsCodeFile.getJsCode());
    }

    private TupleFive<ScriptObjectMirror, CurrentUserWrapper, HttpRequestWrapper, HttpResponseWrapper, HttpSessionWrapper> getCtx(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ScriptObjectMirror ctx = ScriptEngineUtils.newObject();
        HttpRequestWrapper requestWrapper = new HttpRequestWrapper(request);
        HttpResponseWrapper responseWrapper = new HttpResponseWrapper(response, jacksonMapper);
        HttpSession session = request.getSession(false);
        HttpSessionWrapper sessionWrapper = new HttpSessionWrapper(session);
        CurrentUserWrapper currentUserWrapper = new CurrentUserWrapper("lizw", "13260658831");
        // ctx.put("req", requestWrapper.getWrapper());
        ctx.put("req", requestWrapper);
        ctx.put("res", responseWrapper);
        ctx.put("session", sessionWrapper);
        ctx.put("currentUser", currentUserWrapper);
        return TupleFive.creat(ctx, currentUserWrapper, requestWrapper, responseWrapper, sessionWrapper);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        final long startTime1 = System.currentTimeMillis();
        String requestUri = request.getRequestURI();
        String method = request.getMethod();
        boolean supportUri = false;
        for (String suffix : Support_Suffix) {
            if (StringUtils.isBlank(suffix)) {
                supportUri = true;
                continue;
            }
            if (requestUri.endsWith(suffix)) {
                supportUri = true;
                requestUri = requestUri.substring(0, requestUri.length() - suffix.length());
                break;
            }
        }
        String singleMethodHandler = null;
        String allMethodHandler = null;
        if (supportUri) {
            // ...(requestUri)/[post/get/put/delete]_controller
            singleMethodHandler = requestUri + "/" + method.toLowerCase() + "_" + Handler_File_Name;
            // ...(requestUri)/controller
            allMethodHandler = requestUri + "/" + Handler_File_Name;
        }
        // 匹配处理当前请求对应的js文件
        String jsHandlerFileFullName = null;
        if (StringUtils.isNotBlank(singleMethodHandler) && jsCodeFileExists(singleMethodHandler)) {
            jsHandlerFileFullName = singleMethodHandler;
        }
        if (StringUtils.isBlank(jsHandlerFileFullName) && StringUtils.isNotBlank(allMethodHandler) && jsCodeFileExists(allMethodHandler)) {
            jsHandlerFileFullName = allMethodHandler;
        }
        if (StringUtils.isBlank(jsHandlerFileFullName) && StringUtils.isNotBlank(requestUri) && StringUtils.isBlank(FilenameUtils.getExtension(requestUri))) {
            String tmp = requestUri + ".js";
            if (jsCodeFileExists(tmp)) {
                jsHandlerFileFullName = tmp;
            }
        }
        // js请求处理文件不存在
        if (StringUtils.isBlank(jsHandlerFileFullName)) {
            return true;
        }
        // 加载js模块对象处理请求
        final long startTime2 = System.currentTimeMillis();
        final ScriptObjectMirror jsHandler = scriptModuleInstance.useJs(jsHandlerFileFullName);
        Object handlerObject = jsHandler.getMember(Handler_Method);
        if (!(handlerObject instanceof ScriptObjectMirror)) {
            return true;
        }
        ScriptObjectMirror handlerFunction = (ScriptObjectMirror) handlerObject;
        if (!handlerFunction.isFunction()) {
            return true;
        }
        if (handler instanceof HandlerMethod) {
            log.warn("js请求处理函数功能被原生SpringMvc功能覆盖 | {}", jsHandlerFileFullName);
            response.setHeader("use-http-request-js-handler-be-override", jsHandlerFileFullName);
            return true;
        }
        if (!(handler instanceof ResourceHttpRequestHandler)) {
            log.warn("出现意外的handler | {}", handler.getClass());
            return true;
        }
        // 使用js代码处理请求
        final long startTime3 = System.currentTimeMillis();
        response.setHeader("use-http-request-js-handler", jsHandlerFileFullName);
        TupleFive<ScriptObjectMirror, CurrentUserWrapper, HttpRequestWrapper, HttpResponseWrapper, HttpSessionWrapper> tupleFive = getCtx(request, response);
        Object result = jsHandler.callMember(Handler_Method, tupleFive.getValue1());
        final long startTime4 = System.currentTimeMillis();
        tupleFive.getValue4().wrapper();
        boolean needWriteResult = false;
        if (result != null && !(result instanceof Undefined)) {
            needWriteResult = true;
        }
        if (needWriteResult && !tupleFive.getValue4().isWrite()) {
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().println(jacksonMapper.toJson(result));
        }
        long endTime = System.currentTimeMillis();
        log.info(
                "使用js代码处理请求 | [{}] | [总]耗时 {}ms | [Js处理全过程]耗时 {}ms | [Js函数调用]耗时 {}ms | [返回值序列化]耗时 {}ms",
                jsHandlerFileFullName,
                endTime - startTime1,
                endTime - startTime2,
                endTime - startTime3,
                endTime - startTime4
        );
        return false;
    }

//    @Override
//    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
//        log.info("=================================================== postHandle");
//    }
//
//    @Override
//    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
//        log.info("=================================================== afterCompletion");
//    }
}
