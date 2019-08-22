package org.clever.nashorn.internal;

/**
 * Console 实现接口
 * <p>
 * 作者：lizw <br/>
 * 创建时间：2019/08/22 09:28 <br/>
 */
public interface Console {

    void log(Object... args);

    void trace(Object... args);

    void debug(Object... args);

    void info(Object... args);

    void warn(Object... args);

    void error(Object... args);

    /**
     * 创建一个新的 Console
     *
     * @param filePath 文件路径
     * @param fileName 文件名称
     */
    Console creat(String filePath, String fileName);
}
