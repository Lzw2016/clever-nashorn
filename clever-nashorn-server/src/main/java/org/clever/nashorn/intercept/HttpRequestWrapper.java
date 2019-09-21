package org.clever.nashorn.intercept;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.clever.common.utils.mapper.JsonWrapper;
import org.clever.nashorn.utils.ObjectConvertUtils;
import org.clever.nashorn.utils.ScriptEngineUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.*;

/**
 * 作者：lizw <br/>
 * 创建时间：2019/09/20 17:27 <br/>
 */
@Slf4j
public class HttpRequestWrapper extends HashMap<String, Object> {
    /**
     * Cookie的默认编码格式
     */
    private final static String DEFAULT_COOKIE_Encode = "UTF-8";
    /**
     * Http请求对象
     */
    private final HttpServletRequest request;
    // @Getter
    // private final ScriptObjectMirror wrapper = ScriptEngineUtils.newObject();
    private final HashMap<String, Object> wrapper = this;

    public HttpRequestWrapper(HttpServletRequest request) throws IOException {
        this.request = request;
        init();
    }

    /**
     * <pre>
     * method              -   请求方法        get
     * protocol            -   请求协议        HTTP/1.1
     * scheme              -   请求协议        http
     * host                -   服务器主机名     demo.msvc.top
     * hostname            -   服务器主机名     demo.msvc.top
     * localName           -   服务器主机名     demo.msvc.top
     * serverName          -   服务器主机名     demo.msvc.top
     * ip                  -   服务器IP        192.168.33.121
     * localAddr           -   服务器IP        192.168.33.121
     * port                -   服务器端口号     18081
     * serverPort          -   服务器端口号     18081
     * localPort           -   服务器端口号     18081
     * path                -   请求url path    /api/demo_1
     * servletPath         -   请求url path    /api/demo_1
     * uri                 -   请求url path    /api/demo_1
     * queryString         -   查询字符串       a=123a&b=456b
     * url                 -   请求url(protocol、host、path)                    http://demo.msvc.top:18081/api/demo_1
     * href                -   请求完整字符串(protocol、host、path、querystring)  http://demo.msvc.top:18081/api/demo_1?a=123a&b=456b
     * remoteAddr          -   客户端的IP地址    192.168.33.16
     * remoteHost          -   客户端的主机名     192.168.33.16
     * remotePort          -   客户端的端口号     36478
     * remoteUser          -   客户端用户名
     * charset             -   请求编码字符集
     * characterEncoding   -   请求编码字符集
     * type                -   请求Content-Type
     * contentType         -   请求Content-Type
     * length              -   请求Content-Length
     * contentLength       -   请求Content-Length
     * contextPath         -   Servlet ContextPath
     * sessionId           -   Session Id
     * parameters          -   请求参数(form、queryString)
     * headers             -   请求头
     * cookies             -   请求cookies
     * content             -   请求body原始字符串
     * body                -   请求body对象
     * // ---------------------------------------------------------------
     * pathVariables
     * getCookies(name, [options])
     * </pre>
     */
    private void init() throws IOException {
        String method = request.getMethod();
        String url = request.getRequestURL().toString();
        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();
        String servletPath = request.getServletPath();
        String pathInfo = request.getPathInfo();
        String pathTranslated = request.getPathTranslated();
        String authType = request.getAuthType();
        String contentType = request.getContentType();
        String queryString = request.getQueryString();
        String remoteUser = request.getRemoteUser();
        String requestedSessionId = request.getRequestedSessionId();
        String characterEncoding = request.getCharacterEncoding();
        String localAddr = request.getLocalAddr();
        String remoteAddr = request.getRemoteAddr();
        String remoteHost = request.getRemoteHost();
        String localName = request.getLocalName();
        String protocol = request.getProtocol();
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();
        int contentLength = request.getContentLength();
        int remotePort = request.getRemotePort();
        int localPort = request.getLocalPort();
        // --------------------------------------------------------------------- 常用属性
        wrapper.put("method", method);
        wrapper.put("host", localName);
        wrapper.put("hostname", localName);
        wrapper.put("localName", localName);
        wrapper.put("serverName", serverName);
        wrapper.put("ip", localAddr);
        wrapper.put("localAddr", localAddr);
        wrapper.put("port", serverPort);
        wrapper.put("serverPort", serverPort);
        wrapper.put("localPort", localPort);
        wrapper.put("path", servletPath);
        wrapper.put("servletPath", servletPath);
        wrapper.put("uri", uri);
        wrapper.put("protocol", protocol);
        wrapper.put("scheme", scheme);
        wrapper.put("queryString", queryString);
        wrapper.put("url", url);
        String href = String.format("%s?%s", url, queryString);
        wrapper.put("href", href);
        wrapper.put("remoteAddr", remoteAddr);
        wrapper.put("remoteHost", remoteHost);
        wrapper.put("remotePort", remotePort);
        wrapper.put("remoteUser", remoteUser);
        wrapper.put("charset", characterEncoding);
        wrapper.put("characterEncoding", characterEncoding);
        wrapper.put("type", contentType);
        wrapper.put("contentType", contentType);
        wrapper.put("length", contentLength);
        wrapper.put("contentLength", contentLength);
        wrapper.put("contextPath", contextPath);
        if (request.getSession() != null) {
            wrapper.put("sessionId", request.getSession().getId());
        }
        // --------------------------------------------------------------------- 扩展属性
        wrapper.put("authType", authType);
        wrapper.put("pathInfo", pathInfo);
        wrapper.put("pathTranslated", pathTranslated);
        wrapper.put("requestedSessionId", requestedSessionId);
        // --------------------------------------------------------------------- 请求参数(form、queryString)
        Map<String, String[]> parameterMap = request.getParameterMap();
        ScriptObjectMirror parameters = ScriptEngineUtils.newObject();
        parameterMap.forEach((name, values) -> {
            if (values == null) {
                parameters.put(name, null);
                return;
            }
            if (values.length == 1) {
                parameters.put(name, values[0]);
            } else if (values.length >= 1) {
                parameters.put(name, ObjectConvertUtils.Instance.javaToJSObject(values));
            }
        });
        wrapper.put("parameters", parameters);
        // --------------------------------------------------------------------- 请求headers
        Enumeration<String> headerEnumeration = request.getHeaderNames();
        ScriptObjectMirror headers = ScriptEngineUtils.newObject();
        while (headerEnumeration.hasMoreElements()) {
            String headerName = headerEnumeration.nextElement();
            Enumeration<String> values = request.getHeaders(headerName);
            if (values == null) {
                continue;
            }
            List<String> valueList = new ArrayList<>(1);
            while (values.hasMoreElements()) {
                valueList.add(values.nextElement());
            }
            if (valueList.size() <= 0) {
                headers.put(headerName, null);
            } else if (valueList.size() == 1) {
                headers.put(headerName, valueList.get(0));
            } else {
                headers.put(headerName, ObjectConvertUtils.Instance.javaToJSObject(valueList));
            }
        }
        wrapper.put("headers", headers);
        // --------------------------------------------------------------------- 请求cookies
        ScriptObjectMirror cookies = ScriptEngineUtils.newObject();
        Cookie[] cookieArray = request.getCookies();
        if (cookieArray != null) {
            for (Cookie cookie : cookieArray) {
                String value;
                try {
                    value = URLDecoder.decode(cookie.getValue(), DEFAULT_COOKIE_Encode);
                } catch (Throwable e) {
                    log.error("Cookie的值解码失败", e);
                    value = cookie.getValue();
                }
                cookies.put(cookie.getName(), value);
            }
        }
        wrapper.put("cookies", cookies);
        // --------------------------------------------------------------------- 请求body
        String content = IOUtils.toString(request.getInputStream(), characterEncoding);
        wrapper.put("content", content);
        if (StringUtils.isNotBlank(content) && contentType.toLowerCase().contains("application/json")) {
            try {
                JsonWrapper jsonWrapper = new JsonWrapper(content);
                wrapper.put("body", ObjectConvertUtils.Instance.javaToJSObject(jsonWrapper.getInnerMap()));
            } catch (Throwable e) {
                wrapper.put("body", content);
            }
        }
    }
}
