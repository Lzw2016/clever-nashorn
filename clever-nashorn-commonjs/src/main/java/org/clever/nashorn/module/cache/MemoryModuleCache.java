package org.clever.nashorn.module.cache;

import org.clever.nashorn.module.Module;

import java.util.HashMap;
import java.util.Map;

/**
 * Module 内存缓存
 */
public class MemoryModuleCache implements ModuleCache {
    private Map<String, Module> modules = new HashMap<>();

    public Module get(String fullPath) {
        return modules.get(fullPath);
    }

    public void put(String fullPath, Module module) {
        modules.put(fullPath, module);
    }

    public void clear() {
        modules.clear();
    }
}
