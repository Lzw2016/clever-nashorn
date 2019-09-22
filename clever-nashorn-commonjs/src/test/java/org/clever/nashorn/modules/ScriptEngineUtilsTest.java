package org.clever.nashorn.modules;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import lombok.extern.slf4j.Slf4j;
import org.clever.nashorn.utils.ObjectConvertUtils;
import org.clever.nashorn.utils.ScriptEngineUtils;
import org.clever.nashorn.utils.StrFormatter;
import org.junit.Test;

import javax.script.ScriptException;
import java.util.Date;

/**
 * 作者： lzw<br/>
 * 创建时间：2019-09-22 08:31 <br/>
 */
@Slf4j
public class ScriptEngineUtilsTest {

    @Test
    public void t1() {
        ScriptObjectMirror scriptObjectMirror = ScriptEngineUtils.newObject("456b", "aaa");
        log.info("-------------------------> {}", scriptObjectMirror);
    }

    @Test
    public void t2() throws ScriptException {
        ScriptObjectMirror scriptObjectMirror = (ScriptObjectMirror) ScriptEngineUtils.getDefaultEngine().eval("Array");
        Object array = scriptObjectMirror.newObject("456b", "aaa");
        log.info("-------------------------> {}", array);
        array = scriptObjectMirror.newObject(12, 23.666, "asdf");
        log.info("-------------------------> {}", array);
    }

    @Test
    public void t3() throws ScriptException {
        ScriptObjectMirror scriptObjectMirror = (ScriptObjectMirror) ScriptEngineUtils.getDefaultEngine().eval("Date");
        Date date = new Date();
        Object dateObject = scriptObjectMirror.newObject((double) date.getTime());
        log.info("-------------------------> date = {} | dateObject = {} | StrFormatter = {}", date, dateObject, StrFormatter.toString(dateObject));
    }

    @Test
    public void t4() {
        Object date = ObjectConvertUtils.Instance.javaToJSObject(new Date());
        log.info("-------------------------> {} | {}", date, StrFormatter.toString(date));
    }
}
