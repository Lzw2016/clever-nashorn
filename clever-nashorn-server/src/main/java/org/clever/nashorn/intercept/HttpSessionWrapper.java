package org.clever.nashorn.intercept;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.clever.nashorn.utils.ScriptEngineUtils;

import javax.servlet.http.HttpSession;
import java.util.Enumeration;
import java.util.HashMap;

/**
 * 作者：lizw <br/>
 * 创建时间：2019/09/20 17:28 <br/>
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class HttpSessionWrapper extends HashMap<String, Object> {

    /**
     * HttpSession(可能为null)
     */
    private final HttpSession session;

    private final HashMap<String, Object> wrapper = this;

    public HttpSessionWrapper(HttpSession session) {
        this.session = session;
        if (session != null) {
            init();
        }
    }

    @SuppressWarnings("DuplicatedCode")
    private void init() {
        if (session == null) {
            return;
        }
        wrapper.put("id", session.getId());
        wrapper.put("isNew", session.isNew());
        wrapper.put("creationTime", session.getCreationTime());
        wrapper.put("lastAccessedTime", session.getLastAccessedTime());
        wrapper.put("maxInactiveInterval", session.getMaxInactiveInterval());
        // --------------------------------------------------------------------- Session attributes
        ScriptObjectMirror attributes = ScriptEngineUtils.newObject();
        Enumeration<String> attributeNames = session.getAttributeNames();
        if (attributeNames != null) {
            while (attributeNames.hasMoreElements()) {
                String attributeName = attributeNames.nextElement();
                Object attributeValue = session.getAttribute(attributeName);
                attributes.put(attributeName, attributeValue);
            }
        }
        wrapper.put("attributes", attributes);
    }

    public void setAttribute(String name, Object value) {
        if (session == null) {
            return;
        }
        session.setAttribute(name, value);
    }

    public void setMaxInactiveInterval(int interval) {
        if (session == null) {
            return;
        }
        session.setMaxInactiveInterval(interval);
    }
}
