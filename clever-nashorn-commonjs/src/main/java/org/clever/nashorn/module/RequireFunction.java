package org.clever.nashorn.module;

import jdk.nashorn.api.scripting.NashornException;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

import javax.script.ScriptException;

/**
 * 作者： lzw<br/>
 * 创建时间：2019-01-14 17:23 <br/>
 */
@FunctionalInterface
public interface RequireFunction {
    ScriptObjectMirror require(String name) throws ScriptException, NashornException;
}
