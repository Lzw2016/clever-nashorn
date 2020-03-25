package org.clever.nashorn.modules;

import com.google.common.collect.Maps;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.clever.nashorn.ScriptModuleInstance;
import org.clever.nashorn.internal.CommonUtils;
import org.junit.Test;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * 作者： lzw<br/>
 * 创建时间：2019-01-11 14:30 <br/>
 */
@Slf4j
public class DirectoryWatcherTest {
    private static final ResourceLoader LOADER = new DefaultResourceLoader();

    private static final String PATH = "C:\\Users\\lzw\\Desktop\\jztSource\\yvan-jvm-js-script\\yvan-jvm-js-script\\src\\test\\resources";

    @Test
    public void t02() throws Exception {
        FileAlterationMonitor monitor = new FileAlterationMonitor(1000);
        FileAlterationObserver observer = new FileAlterationObserver(PATH);
        monitor.addObserver(observer);
        observer.addListener(new FileListener());
        monitor.start();

        Thread.sleep(1000 * 1000);
        monitor.stop();
    }

    @Test
    public void t03() throws IOException {
        final String path = LOADER.getResource("classpath:/typescript/tsconfig.json").getFile().getAbsolutePath();

        log.info("--> {} | {}", path, FilenameUtils.concat(path, "../build"));
    }

    @Test
    public void t05() throws Exception {
        final String path = LOADER.getResource("classpath:/typescript/tsconfig.json").getFile().getAbsolutePath();
        final String root = FilenameUtils.concat(path, "../build");

        ScriptModuleInstance scriptModuleInstance = ScriptModuleInstance.creatDefault("test", "test", root);
        ScriptObjectMirror scriptObjectMirror = scriptModuleInstance.useJs("./test.js");
        log.info(" # --- {}", scriptObjectMirror.callMember("addNumberWrapper", 1, 2));

        Thread.sleep(1000 * 10);

        scriptModuleInstance.getModuleCache().remove(FilenameUtils.concat(root, "./test.js"));
        scriptObjectMirror = scriptModuleInstance.useJs("./test.js");
        log.info(" # --- {}", scriptObjectMirror.callMember("addNumberWrapper", 1, 2));
    }

    @Test
    public void t06() throws Exception {
        final String root = "D:\\SourceCode\\clever\\clever-nashorn\\clever-nashorn-commonjs\\src\\test\\resources\\typescript\\build";

        FileAlterationMonitor monitor = new FileAlterationMonitor(1000);
        FileAlterationObserver observer = new FileAlterationObserver(root);
        FileListener fileListener = new FileListener();
        monitor.addObserver(observer);
        observer.addListener(fileListener);
        monitor.start();

        Map<String, Object> context = Maps.newLinkedHashMap();
        context.put("CommonUtils", CommonUtils.Instance);
        ScriptModuleInstance scriptModuleInstance = ScriptModuleInstance.creatDefault("test", "test", root,context);
        fileListener.setScriptModuleInstance(scriptModuleInstance);

        new Thread(()->{
            log.info("start v1=========");
            ScriptObjectMirror scriptObjectMirror = scriptModuleInstance.useJs("./test.js");
            log.info(" # --- {}", scriptObjectMirror.callMember("testA"));
        }).start();

        Thread.sleep(5000);

        new Thread(()->{
            log.info("start v2=========");
            ScriptObjectMirror scriptObjectMirror = scriptModuleInstance.useJs("./test.js");
            log.info(" # --- {}", scriptObjectMirror.callMember("testA"));
        }).start();

//        for (int i = 0; i <= 100; i++) {
//            Thread.sleep(1000 * 3);
//            ScriptObjectMirror scriptObjectMirror = scriptModuleInstance.useJs("./test.js");
//            // log.info(" # --- {}", scriptObjectMirror.callMember("addNumberWrapper", 1, i));
//        }

        Thread.sleep(Integer.MAX_VALUE);

        monitor.stop();
    }

    public static class FileListener implements FileAlterationListener {
        @Setter
        private ScriptModuleInstance scriptModuleInstance;

        @Override
        public void onStart(FileAlterationObserver observer) {
//            log.info(" {}", "onStart");
        }

        @Override
        public void onDirectoryCreate(File directory) {
            log.info("onDirectoryCreate {}", directory.getName());
        }

        @Override
        public void onDirectoryChange(File directory) {
            log.info("onDirectoryChange {}", directory.getName());
        }

        @Override
        public void onDirectoryDelete(File directory) {
            log.info("onDirectoryDelete {}", directory.getName());
        }

        @Override
        public void onFileCreate(File file) {
            log.info("onFileCreate {}", file.getName());
        }

        @Override
        public void onFileChange(File file) {
            remove(file);
            log.info("onFileChange {}", file.getName());
        }

        @Override
        public void onFileDelete(File file) {
            remove(file);
            log.info("onFileDelete {}", file.getName());
        }

        @Override
        public void onStop(FileAlterationObserver observer) {
//            log.info(" {}", "onStop");
        }

        public void remove(File file) {
            if (scriptModuleInstance == null) {
                return;
            }
//             scriptModuleInstance.getModuleCache().remove(file.getAbsolutePath());
            scriptModuleInstance.getModuleCache().clear();
        }
    }
}
