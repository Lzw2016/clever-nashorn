package org.clever.nashorn.modules;

import jdk.nashorn.api.scripting.NashornScriptEngine;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;

/**
 * 作者： lzw<br/>
 * 创建时间：2019-01-10 12:01 <br/>
 */
@Slf4j
public class MyTest {

    private NashornScriptEngine getEngine() {
        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        NashornScriptEngine engine = (NashornScriptEngine) scriptEngineManager.getEngineByName("nashorn");
        log.info("# getEngine -> {}", engine);
        return engine;
    }

    // 循环依赖 test4 - cycles
    @Test
    public void test4Cycles() throws ScriptException {
        NashornScriptEngine engine = getEngine();
        Folder rootFolder = FilesystemFolder.create(new File("src/test/resources/test4/cycles"));
        Module rootModule = Require.enable(engine, rootFolder);
        ScriptObjectMirror scriptObjectMirror = rootModule.useJs("./main.js");
        log.info("### scriptObjectMirror - {}", scriptObjectMirror);
    }

    // 循环依赖 test4 - deep
    @Test
    public void test4Deep() throws ScriptException {
        NashornScriptEngine engine = getEngine();
        Folder rootFolder = FilesystemFolder.create(new File("src/test/resources/test4/deep"));
        Module rootModule = Require.enable(engine, rootFolder);
        ScriptObjectMirror scriptObjectMirror = rootModule.useJs("./main.js");
        log.info("### scriptObjectMirror - {}", scriptObjectMirror);
    }

    // 循环依赖 test4 - demo3
    @Test
    public void test4Demo3() throws ScriptException {
        NashornScriptEngine engine = getEngine();
        Folder rootFolder = FilesystemFolder.create(new File("src/test/resources/test4/demo3"));
        Module rootModule = Require.enable(engine, rootFolder);
        ScriptObjectMirror scriptObjectMirror = rootModule.useJs("./main.js");
        log.info("### --------------------------------------------------------------------- scriptObjectMirror - {}", scriptObjectMirror);
        ScriptObjectMirror a = rootModule.useJs("./a.js");
        a.callMember("aFuc");
        ScriptObjectMirror b = rootModule.useJs("./b.js");
        b.callMember("bFuc");
    }

    @Test
    public void test1() throws ScriptException {
        NashornScriptEngine engine = getEngine();
        Folder rootFolder = FilesystemFolder.create(new File("src/test/resources/test1"));
        Module rootModule = Require.enable(engine, rootFolder);
        ScriptObjectMirror scriptObjectMirror = rootModule.useJs("./foo.js");
        scriptObjectMirror.callMember("test", "nashorn");
        log.info(" # --- {}", rootModule);
    }

    @Test
    public void test6() throws ScriptException, InterruptedException {
        NashornScriptEngine engine = getEngine();
        Folder rootFolder = FilesystemFolder.create(new File("src/test/resources/test6"));
        Module rootModule = Require.enable(engine, rootFolder);
        ScriptObjectMirror scriptObjectMirror = rootModule.useJs("./t01");
        scriptObjectMirror.callMember("test");
        Thread.sleep(1000 * 10);
        rootModule.getModuleCache().clear();
        log.info(" # --- {}", "clear");
        scriptObjectMirror = rootModule.useJs("./t01");
        scriptObjectMirror.callMember("test");
        log.info(" # --- {}", rootModule);
    }

    // ========================================================================================================================================================

    @Test
    public void tmp() throws ScriptException {
        NashornScriptEngine engine = getEngine();
        Folder rootFolder = FilesystemFolder.create(new File("src/test/resources/tmp"));
        Module rootModule = Require.enable(engine, rootFolder);
        ScriptObjectMirror scriptObjectMirror = rootModule.useJs("./index.js");
        scriptObjectMirror.callMember("test", "nashorn");
        log.info(" # --- {}", rootModule);
    }
}
