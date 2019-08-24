package org.clever.nashorn.modules;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import lombok.extern.slf4j.Slf4j;
import org.clever.nashorn.ScriptModuleInstance;
import org.clever.nashorn.internal.LogConsole;
import org.clever.nashorn.utils.StrFormatter;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 作者：lizw <br/>
 * 创建时间：2019/08/22 09:52 <br/>
 */
@Slf4j
public class LogConsoleTest {

    private LogConsole logConsole = new LogConsole("/", "test");

    @Test
    public void t1() {
        logConsole.log("{}", "1234567890");
        logConsole.log("");
    }

    @Test
    public void t2() {
        List<Object> list = StrFormatter.formatToList("111{}222{}333{}444{}555", true, "AAA", 100, new String[]{"BBB", "CCC"});
        log.info(String.valueOf(list));
    }

    @Test
    public void t3() {
        Map<String, Object> context = new HashMap<>();
        ScriptModuleInstance scriptModuleInstance = ScriptModuleInstance.creatDefault("src/test/resources/tmp", context);
        ScriptObjectMirror scriptObjectMirror = scriptModuleInstance.useJs("./t3.js");
        scriptObjectMirror.callMember("test", 1);
        scriptObjectMirror.callMember("test", 2.2F);
        scriptObjectMirror.callMember("test", 3.3D);
        scriptObjectMirror.callMember("test", 4L);
        scriptObjectMirror.callMember("test", false);
        scriptObjectMirror.callMember("test", "nashorn");
        scriptObjectMirror.callMember("test", new Date());

        Map<String, Object> tmp = new HashMap<>();
        tmp.put("1", 1);
        tmp.put("2", 2.2F);
        tmp.put("3", 3.3D);
        tmp.put("4", 4L);
        tmp.put("5", false);
        tmp.put("6", "nashorn");
        tmp.put("7", new Date());
        tmp.put("8", new Object[]{1, 2.2F, 3.3D, 4L, true, "nashorn"});
        scriptObjectMirror.callMember("test", tmp);
        log.info(" # --- {}", scriptModuleInstance.getRootModule());
    }
}
