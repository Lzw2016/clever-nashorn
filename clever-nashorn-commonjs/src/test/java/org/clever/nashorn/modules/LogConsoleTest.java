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

    @SuppressWarnings("ConfusingArgumentToVarargsMethod")
    @Test
    public void t3() {
        Map<String, Object> context = new HashMap<>();
        ScriptModuleInstance scriptModuleInstance = ScriptModuleInstance.creatDefault("src/test/resources/tmp", context);
        ScriptObjectMirror scriptObjectMirror = scriptModuleInstance.useJs("./t3.js");
        scriptObjectMirror.callMember("printJava", null);
        scriptObjectMirror.callMember("printJava", 1);
        scriptObjectMirror.callMember("printJava", 2.2F);
        scriptObjectMirror.callMember("printJava", 3.3D);
        scriptObjectMirror.callMember("printJava", 4L);
        scriptObjectMirror.callMember("printJava", false);
        scriptObjectMirror.callMember("printJava", "nashorn");
        scriptObjectMirror.callMember("printJava", new Date());
        Map<String, Object> tmp = new HashMap<>();
        tmp.put("null", null);
        tmp.put("int", 1);
        tmp.put("float", 2.2F);
        tmp.put("double", 3.3D);
        tmp.put("long", 4L);
        tmp.put("boolean", false);
        tmp.put("string", "nashorn");
        tmp.put("date", new Date());
        tmp.put("array", new Object[]{1, 2.2F, 3.3D, 4L, true, "nashorn", null, new Date()});
        scriptObjectMirror.callMember("printJava", tmp);
        scriptObjectMirror.callMember("printJS");
        log.info(" # --- {}", scriptModuleInstance.getRootModule());
    }

    @SuppressWarnings("ConfusingArgumentToVarargsMethod")
    @Test
    public void tmp() {
        Map<String, Object> context = new HashMap<>();
        ScriptModuleInstance scriptModuleInstance = ScriptModuleInstance.creatDefault("src/test/resources/tmp", context);
        ScriptObjectMirror scriptObjectMirror = scriptModuleInstance.useJs("./t3.js");
        scriptObjectMirror.callMember("tmp", null);
        log.info(" # --- {}", scriptModuleInstance.getRootModule());
    }

    @Test
    public void t4() {
        // 打印语法错误位置(行列)
        Map<String, Object> context = new HashMap<>();
        ScriptModuleInstance scriptModuleInstance = ScriptModuleInstance.creatDefault("src/test/resources/tmp", context);
        ScriptObjectMirror scriptObjectMirror = scriptModuleInstance.useJs("./t4.js");
        scriptObjectMirror.callMember("tmp");
        log.info(" # --- {}", scriptModuleInstance.getRootModule());
    }
}
