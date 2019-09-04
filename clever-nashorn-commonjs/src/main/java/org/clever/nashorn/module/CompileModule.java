package org.clever.nashorn.module;

import org.clever.nashorn.folder.Folder;

/**
 * 作者： lzw<br/>
 * 创建时间：2019-09-04 21:58 <br/>
 */
public interface CompileModule {

    Module compileJsonModule(Folder path, String filename, String scriptCode);

    Module compileJavaScriptModule(Folder path, String filename, String scriptCode);
}
