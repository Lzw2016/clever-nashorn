package org.clever.nashorn.modules;

import java.util.HashMap;
import java.util.Map;

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
