package org.clever.nashorn.intercept;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

/**
 * 作者：lizw <br/>
 * 创建时间：2019/09/20 17:28 <br/>
 */
public class HttpResponseWrapper extends HashMap<String, Object> {

    /**
     * Http响应对象
     */
    private final HttpServletResponse response;

    private final HashMap<String, Object> wrapper = this;

    public HttpResponseWrapper(HttpServletResponse response) {
        this.response = response;
        init();
    }

    private void init() {

    }
}
