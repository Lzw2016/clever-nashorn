package org.clever.nashorn.intercept;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import jdk.nashorn.internal.runtime.Undefined;
import lombok.Getter;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.clever.common.utils.CookieUtils;
import org.clever.common.utils.mapper.JacksonMapper;
import org.clever.nashorn.utils.ScriptEngineUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 作者：lizw <br/>
 * 创建时间：2019/09/20 17:28 <br/>
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class HttpResponseWrapper extends HashMap<String, Object> {
    /**
     * Http响应对象
     */
    private final HttpServletResponse response;
    /**
     * 响应数据序列化
     */
    private final JacksonMapper jacksonMapper;
    /**
     * 是否写入数据到输出流
     */
    @Getter
    private boolean write = false;

    private final HashMap<String, Object> wrapper = this;

    private final ScriptObjectMirror header = ScriptEngineUtils.newObject();

    private final ScriptObjectMirror cookies = ScriptEngineUtils.newObject();

    private Object body = null;

    public HttpResponseWrapper(HttpServletResponse response, JacksonMapper jacksonMapper) {
        this.response = response;
        this.jacksonMapper = jacksonMapper;
        init();
    }

    private void init() {
        wrapper.put("status", 200);
        wrapper.put("header", header);
        wrapper.put("cookies", cookies);
        wrapper.put("body", body);
    }

    /**
     * 重定向
     */
    public void redirect(String url) throws IOException {
        response.sendRedirect(url);
    }

    /**
     * 写入响应数据
     */
    public void write(Object body) throws IOException {
        if (body == null) {
            return;
        }
        write = true;
        if (body instanceof String) {
            response.getOutputStream().print(body.toString());
        } else {
            response.getOutputStream().print(jacksonMapper.toJson(body));
        }
    }

    /**
     * 适配响应对象
     */
    @SuppressWarnings({"UnusedReturnValue", "WeakerAccess"})
    public HttpServletResponse wrapper() throws IOException {
        // --------------------------------------------------------------------- 设置响应状态码
        Object status = wrapper.getOrDefault("status", 200);
        int statusInt = NumberUtils.toInt(String.valueOf(status), 200);
        response.setStatus(statusInt);
        // --------------------------------------------------------------------- 响应头设置
        Object header = wrapper.getOrDefault("header", null);
        if (header instanceof Map) {
            Map<?, ?> headerMap = (Map) header;
            headerMap.forEach((key, value) -> {
                if (value != null && key instanceof String && StringUtils.isNotBlank((String) key)) {
                    String hValue;
                    if (value instanceof String) {
                        hValue = (String) value;
                    } else {
                        hValue = jacksonMapper.toJson(value);
                    }
                    response.setHeader(String.valueOf(key), hValue);
                }
            });
        }
        // --------------------------------------------------------------------- 响应cookies设置
        Object cookies = wrapper.getOrDefault("cookies", null);
        if (cookies instanceof Map) {
            Map<?, ?> cookiesMap = (Map) cookies;
            cookiesMap.forEach((key, value) -> {
                if (value != null && key instanceof String && StringUtils.isNotBlank((String) key)) {
                    if (value instanceof String) {
                        CookieUtils.setCookie(response, (String) key, (String) value);
                    } else if (value instanceof Map) {
                        Map<?, ?> valueMap = (Map) value;
                        Object cValue = valueMap.get("value");
                        Object cMaxAge = valueMap.get("maxAge");
                        Object cPath = valueMap.get("path");
                        String cValueStr;
                        if (cValue instanceof String) {
                            cValueStr = (String) cValue;
                        } else {
                            cValueStr = jacksonMapper.toJson(cValue);
                        }
                        String cPathStr;
                        if (cPath instanceof String) {
                            cPathStr = (String) cPath;
                        } else {
                            cPathStr = null;
                        }
                        CookieUtils.setCookie(response, cPathStr, (String) key, cValueStr, NumberUtils.toInt(String.valueOf(cMaxAge), -1));
                    }
                }
            });
        }
        // --------------------------------------------------------------------- 响应body设置
        if (!write) {
            Object body = wrapper.getOrDefault("body", null);
            if (body != null && !(body instanceof Undefined)) {
                String bodyStr;
                if (body instanceof String && StringUtils.isNotBlank((String) body)) {
                    bodyStr = (String) body;
                } else {
                    bodyStr = jacksonMapper.toJson(body);
                }
                if (StringUtils.isNotBlank(bodyStr)) {
                    response.getOutputStream().print(bodyStr);
                    write = true;
                }
            }
        }
        return response;
    }
}