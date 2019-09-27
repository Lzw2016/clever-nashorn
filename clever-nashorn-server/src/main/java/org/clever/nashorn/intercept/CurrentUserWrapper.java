package org.clever.nashorn.intercept;

import java.util.HashMap;

/**
 * 作者：lizw <br/>
 * 创建时间：2019/09/20 17:28 <br/>
 */
@SuppressWarnings("WeakerAccess")
public class CurrentUserWrapper extends HashMap<String, Object> {

    private final HashMap<String, Object> wrapper = this;

    public CurrentUserWrapper(String username, String telephone) {
        init(username, telephone);
    }

    private void init(String username, String telephone) {
        wrapper.put("username", username);
        wrapper.put("telephone", telephone);
    }
}
