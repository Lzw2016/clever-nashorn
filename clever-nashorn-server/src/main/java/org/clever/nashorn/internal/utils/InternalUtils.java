package org.clever.nashorn.internal.utils;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

/**
 * 作者：lizw <br/>
 * 创建时间：2019/10/10 13:28 <br/>
 */
public class InternalUtils {

    /**
     * 读取回调函数
     */
    public static ScriptObjectMirror getCallback(ScriptObjectMirror scriptObjectMirror) {
        if (scriptObjectMirror == null) {
            throw new RuntimeException("没有回调函数");
        }
        ScriptObjectMirror callback = null;
        if (scriptObjectMirror.isFunction()) {
            callback = scriptObjectMirror;
        } else {
            Object tmp = scriptObjectMirror.get("callback");
            if (tmp instanceof ScriptObjectMirror && ((ScriptObjectMirror) tmp).isFunction()) {
                callback = (ScriptObjectMirror) tmp;
            }
        }
        if (callback == null) {
            throw new RuntimeException("没有回调函数");
        }
        return callback;
    }
}
