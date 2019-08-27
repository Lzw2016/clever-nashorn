package org.clever.nashorn.module.cache;

import org.clever.nashorn.module.Module;

/**
 * 空缓存(不使用缓存)
 * <p>
 * 作者：lizw <br/>
 * 创建时间：2019/08/27 10:35 <br/>
 */
public class EmptyModuleCache implements ModuleCache {

    @Override
    public Module get(String fullPath) {
        return null;
    }

    @Override
    public void put(String fullPath, Module module) {
    }

    @Override
    public void clear() {
    }
}
