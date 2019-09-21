package org.clever.nashorn.module.cache;

import org.clever.nashorn.module.Module;

/**
 * Module 缓存
 */
public interface ModuleCache {

    Module get(String fullPath);

    void put(String fullPath, Module module);

    void clear();

    void remove(String fullPath);
}
