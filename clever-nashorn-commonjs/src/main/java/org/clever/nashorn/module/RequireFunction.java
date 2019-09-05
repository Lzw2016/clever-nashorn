package org.clever.nashorn.module;

import jdk.nashorn.api.scripting.NashornException;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

import javax.script.ScriptException;

/**
 * 加载内部依赖模块
 * <p>
 * 作者： lzw<br/>
 * 创建时间：2019-01-14 17:23 <br/>
 */
@FunctionalInterface
public interface RequireFunction {
    /**
     * 加载内部依赖模块
     *
     * @param name 模块全路径
     */
    ScriptObjectMirror require(String name) throws ScriptException, NashornException;
}
