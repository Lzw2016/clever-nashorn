package org.clever.nashorn.module;

import jdk.nashorn.api.scripting.NashornException;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

import javax.script.ScriptException;

/**
 * 作者： lzw<br/>
 * 创建时间：2019-09-04 20:54 <br/>
 */
@FunctionalInterface
public interface RequireLibFunction {
    /**
     * 加载第三方依赖模块
     *
     * @param name 模块全路径
     */
    ScriptObjectMirror requireLib(String name) throws ScriptException, NashornException;
}
