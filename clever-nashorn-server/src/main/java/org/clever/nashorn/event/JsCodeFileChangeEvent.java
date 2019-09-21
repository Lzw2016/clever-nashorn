package org.clever.nashorn.event;

import lombok.Getter;
import org.clever.nashorn.entity.JsCodeFile;
import org.springframework.context.ApplicationEvent;

/**
 * 作者： lzw<br/>
 * 创建时间：2019-09-21 10:32 <br/>
 */
public class JsCodeFileChangeEvent extends ApplicationEvent {

    @Getter
    private final JsCodeFileChangeEnum change;
    @Getter
    private final JsCodeFile jsCodeFile;

    public JsCodeFileChangeEvent(Object source, JsCodeFileChangeEnum change, JsCodeFile jsCodeFile) {
        super(source);
        this.change = change;
        this.jsCodeFile = jsCodeFile;
    }
}
