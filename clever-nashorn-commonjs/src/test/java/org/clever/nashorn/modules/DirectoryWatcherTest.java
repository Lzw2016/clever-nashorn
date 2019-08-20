package org.clever.nashorn.modules;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.junit.Test;

import java.io.File;

/**
 * 作者： lzw<br/>
 * 创建时间：2019-01-11 14:30 <br/>
 */
@Slf4j
public class DirectoryWatcherTest {
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

    public static class FileListener implements FileAlterationListener {
        @Override
        public void onStart(FileAlterationObserver observer) {
            log.info(" {}", "onStart");
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
            log.info("onFileChange {}", file.getName());
        }

        @Override
        public void onFileDelete(File file) {
            log.info("onFileDelete {}", file.getName());
        }

        @Override
        public void onStop(FileAlterationObserver observer) {
            log.info(" {}", "onStop");
        }
    }
}
