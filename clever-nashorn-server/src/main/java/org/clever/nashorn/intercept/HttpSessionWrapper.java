package org.clever.nashorn.intercept;

import javax.servlet.http.HttpSession;
import java.util.HashMap;

/**
 * 作者：lizw <br/>
 * 创建时间：2019/09/20 17:28 <br/>
 */
public class HttpSessionWrapper extends HashMap<String, Object> {

    /**
     * Http响应对象(可能为null)
     */
    private final HttpSession session;

    private final HashMap<String, Object> wrapper = this;

    public HttpSessionWrapper(HttpSession session) {
        this.session = session;
        if (session != null) {
            init();
        }
    }

    private void init() {

    }
}
