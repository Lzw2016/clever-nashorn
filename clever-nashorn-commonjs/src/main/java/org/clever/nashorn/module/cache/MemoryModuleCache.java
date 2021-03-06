package org.clever.nashorn.module.cache;

import org.clever.nashorn.module.Module;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Module 内存缓存
 */
public class MemoryModuleCache implements ModuleCache {
    /**
     * 缓存map
     */
    private Map<String, Module> modules = new ConcurrentHashMap<>();
    /**
     * 定时清除缓存的时间间隔,毫秒(小于等于0表示不清除)
     */
    private final long clearTimeInterval;
    /**
     * 最后一次清除缓存时间
     */
    private long lastClearTime = System.currentTimeMillis();

    /**
     * @param clearTimeInterval 定时清除缓存的时间间隔,毫秒(小于等于0表示不清除)
     */
    public MemoryModuleCache(long clearTimeInterval) {
        this.clearTimeInterval = clearTimeInterval;
    }

    public MemoryModuleCache() {
        this(-1);
    }

    @Override
    public Module get(String fullPath) {
        intervalClear();
        return modules.get(fullPath);
    }

    @Override
    public void put(String fullPath, Module module) {
        intervalClear();
        modules.put(fullPath, module);
    }

    @Override
    public void clear() {
        modules.clear();
        lastClearTime = System.currentTimeMillis();
    }

    @Override
    public void remove(String fullPath) {
        intervalClear();
        modules.remove(fullPath);
    }

    private void intervalClear() {
        if (clearTimeInterval <= 0) {
            return;
        }
        if ((System.currentTimeMillis() - lastClearTime) >= clearTimeInterval) {
            clear();
        }
    }
}
