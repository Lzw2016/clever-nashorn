package org.clever.nashorn.module;

import org.clever.nashorn.folder.Folder;

import javax.script.ScriptException;

/**
 * 作者： lzw<br/>
 * 创建时间：2019-09-04 21:58 <br/>
 */
public interface CompileModule {

    /**
     * 编译 Json Module
     */
    Module compileJsonModule(Folder path, String filename, String scriptCode) throws ScriptException;

    /**
     * 编译 JavaScript Module
     */
    Module compileJavaScriptModule(Folder path, String filename, String scriptCode) throws ScriptException;
}
