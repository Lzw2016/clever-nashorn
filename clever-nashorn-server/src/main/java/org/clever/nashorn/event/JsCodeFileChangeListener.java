package org.clever.nashorn.event;

import lombok.extern.slf4j.Slf4j;
import org.clever.nashorn.cache.MemoryJsCodeFileCache;
import org.clever.nashorn.entity.JsCodeFile;
import org.clever.nashorn.module.cache.ModuleCache;
import org.clever.nashorn.utils.JsCodeFilePathUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * 作者： lzw<br/>
 * 创建时间：2019-09-21 10:40 <br/>
 */
@Slf4j
@Component
public class JsCodeFileChangeListener implements ApplicationListener<JsCodeFileChangeEvent> {

    @Qualifier("HttpRequestJsHandler-JsCodeFileCache")
    @Autowired
    private MemoryJsCodeFileCache memoryJsCodeFileCache;

    @Qualifier("HttpRequestJsHandler-ModuleCache")
    @Autowired
    private ModuleCache moduleCache;

    @Override
    public void onApplicationEvent(JsCodeFileChangeEvent event) {
        final JsCodeFile jsCodeFile = event.getJsCodeFile();
        if (jsCodeFile == null) {
            return;
        }
        String fullPath = JsCodeFilePathUtils.concat(jsCodeFile.getFilePath(), jsCodeFile.getName());
        log.info("js代码发生变化 | {} | {}", event.getChange(), fullPath);
        switch (event.getChange()) {
            case Add:
                memoryJsCodeFileCache.put(jsCodeFile);
                break;
            case Update:
                memoryJsCodeFileCache.put(jsCodeFile);
                moduleCache.remove(fullPath);
                break;
            case Delete:
                memoryJsCodeFileCache.remove(jsCodeFile);
                moduleCache.remove(fullPath);
        }
    }
}
