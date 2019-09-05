package org.clever.nashorn.modules;

import jdk.nashorn.api.scripting.NashornScriptEngine;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import lombok.extern.slf4j.Slf4j;
import org.clever.nashorn.ScriptModuleInstance;
import org.clever.nashorn.utils.ScriptEngineUtils;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * 作者： lzw<br/>
 * 创建时间：2019-01-10 12:01 <br/>
 */
@Slf4j
public class MyTest {

    private NashornScriptEngine getEngine() {
        return ScriptEngineUtils.creatEngine();
    }

    @Test
    public void test() {
        NashornScriptEngine engine1 = getEngine();
        NashornScriptEngine engine2 = getEngine();
        // 每次获取的Engine对象不一样
        log.info("### engine1={} | engine2={}", engine1, engine2);
    }

    @Test
    public void test1() {
        ScriptModuleInstance scriptModuleInstance = ScriptModuleInstance.creatDefault("src/test/resources/test1");
        ScriptObjectMirror scriptObjectMirror = scriptModuleInstance.useJs("./foo.js");
        scriptObjectMirror.callMember("test", "nashorn");
        log.info(" # --- {}", scriptModuleInstance.getRootModule());
    }

    @Test
    public void test3() {
        ScriptModuleInstance scriptModuleInstance = ScriptModuleInstance.creatDefault("src/test/resources/test3");
        ScriptObjectMirror scriptObjectMirror = scriptModuleInstance.useJs("./test.js");
        log.info(" # --- {}", scriptObjectMirror.get("invariant"));
    }

    // 循环依赖 test4 - cycles
    @Test
    public void test4Cycles() {
        ScriptModuleInstance scriptModuleInstance = ScriptModuleInstance.creatDefault("src/test/resources/test4/cycles");
        ScriptObjectMirror scriptObjectMirror = scriptModuleInstance.useJs("./main.js");
        log.info("### scriptObjectMirror - {}", scriptObjectMirror);
    }

    // 循环依赖 test4 - deep
    @Test
    public void test4Deep() {
        ScriptModuleInstance scriptModuleInstance = ScriptModuleInstance.creatDefault("src/test/resources/test4/deep");
        ScriptObjectMirror scriptObjectMirror = scriptModuleInstance.useJs("./main.js");
        log.info("### scriptObjectMirror - {}", scriptObjectMirror);
    }

    // 循环依赖 test4 - demo3
    @Test
    public void test4Demo3() {
        ScriptModuleInstance scriptModuleInstance = ScriptModuleInstance.creatDefault("src/test/resources/test4/demo3");
        ScriptObjectMirror scriptObjectMirror = scriptModuleInstance.useJs("./main.js");
        log.info("### --------------------------------------------------------------------- scriptObjectMirror - {}", scriptObjectMirror);
        ScriptObjectMirror a = scriptModuleInstance.useJs("./a.js");
        a.callMember("aFuc");
        ScriptObjectMirror b = scriptModuleInstance.useJs("./b.js");
        b.callMember("bFuc");
    }

    @Test
    public void test6() throws InterruptedException {
        ScriptModuleInstance scriptModuleInstance = ScriptModuleInstance.creatDefault("src/test/resources/test6");
        ScriptObjectMirror scriptObjectMirror = scriptModuleInstance.useJs("./t01");
        scriptObjectMirror.callMember("test");
        Thread.sleep(1000 * 3);
        scriptModuleInstance.getModuleCache().clear();
        log.info(" # --- {}", "clear");
        scriptObjectMirror = scriptModuleInstance.useJs("./t01");
        scriptObjectMirror.callMember("test");
        log.info(" # --- {}", scriptModuleInstance);
    }

    @Test
    public void testLib() {
        ScriptModuleInstance scriptModuleInstance = ScriptModuleInstance.creatDefault("src/test/resources/lib");
        ScriptObjectMirror scriptObjectMirror = scriptModuleInstance.useJs("./index");
    }

    // ========================================================================================================================================================

    @Test
    public void tmp1() {
        ScriptModuleInstance scriptModuleInstance = ScriptModuleInstance.creatDefault("src/test/resources/tmp");
        ScriptObjectMirror scriptObjectMirror = scriptModuleInstance.useJs("./index.js");
        scriptObjectMirror.callMember("test", "nashorn");
        log.info(" # --- {}", scriptModuleInstance.getRootModule());
    }

    @Test
    public void tmp2() {
        Map<String, Object> context = new HashMap<>();
        context.put("abcd", new MyTest());
        ScriptModuleInstance scriptModuleInstance = ScriptModuleInstance.creatDefault("src/test/resources/tmp", context);
        scriptModuleInstance.useJs("./index.js");
        log.info(" # --- {}", scriptModuleInstance.getRootModule());
    }

    public void callBack() {
        log.info("### callBack");
    }

    // ========================================================================================================================================================

    @Test
    public void test7() {
        ScriptModuleInstance scriptModuleInstance = ScriptModuleInstance.creatDefault("src/test/resources/test7");
        ScriptObjectMirror scriptObjectMirror = scriptModuleInstance.useJs("./t01");
        final long start = System.currentTimeMillis();
        Object res;
        for (int i = 1; i <= 10000; i++) {
            res = scriptObjectMirror.callMember("test7");
        }
        // 相同的脚本运行J2V8上10000次只需要1秒
        log.info("----> {}", (System.currentTimeMillis() - start) / 1000.0);
    }
}
