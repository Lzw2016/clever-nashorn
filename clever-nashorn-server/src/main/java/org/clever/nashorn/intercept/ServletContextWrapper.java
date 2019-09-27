package org.clever.nashorn.intercept;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import lombok.Getter;
import org.clever.common.utils.mapper.JacksonMapper;
import org.clever.nashorn.utils.ScriptEngineUtils;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;

/**
 * 作者： lzw<br/>
 * 创建时间：2019-09-27 21:43 <br/>
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class ServletContextWrapper extends HashMap<String, Object> {
    /**
     * ServletContext(可能为null)
     */
    private final ServletContext servletContext;

    private final HashMap<String, Object> wrapper = this;

    @Getter
    private final CurrentUserWrapper currentUserWrapper;
    @Getter
    private final HttpRequestWrapper requestWrapper;
    @Getter
    private final HttpSessionWrapper sessionWrapper;
    @Getter
    private final HttpResponseWrapper responseWrapper;

    public ServletContextWrapper(HttpServletRequest request, HttpServletResponse response, JacksonMapper jacksonMapper) throws IOException {
        this.currentUserWrapper = new CurrentUserWrapper("lizw", "13260658831");
        this.requestWrapper = new HttpRequestWrapper(request);
        this.responseWrapper = new HttpResponseWrapper(response, jacksonMapper);
        HttpSession session = request.getSession(false);
        if (session == null) {
            this.sessionWrapper = null;
            this.servletContext = null;
        } else {
            this.sessionWrapper = new HttpSessionWrapper(session);
            this.servletContext = session.getServletContext();
        }
        if (servletContext != null) {
            init();
        }
    }

    @SuppressWarnings("DuplicatedCode")
    private void init() {
        wrapper.put("currentUser", currentUserWrapper);
        wrapper.put("req", requestWrapper);
        wrapper.put("res", responseWrapper);
        wrapper.put("session", sessionWrapper);
        if (servletContext != null) {
            // --------------------------------------------------------------------- ServletContext attributes
            ScriptObjectMirror attributes = ScriptEngineUtils.newObject();
            Enumeration<String> attributeNames = servletContext.getAttributeNames();
            if (attributeNames != null) {
                while (attributeNames.hasMoreElements()) {
                    String attributeName = attributeNames.nextElement();
                    Object attributeValue = servletContext.getAttribute(attributeName);
                    attributes.put(attributeName, attributeValue);
                }
            }
            wrapper.put("attributes", attributes);
        }
    }

    public void setAttribute(String name, Object value) {
        if (servletContext == null) {
            return;
        }
        servletContext.setAttribute(name, value);
    }
}
