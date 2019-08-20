package org.clever.nashorn.modules;

/**
 * Module 缓存
 */
public interface ModuleCache {

    Module get(String fullPath);

    void put(String fullPath, Module module);

    void clear();
}
