package org.clever.nashorn.internal;

/**
 * 基本工具类
 * <p>
 * 作者：lizw <br/>
 * 创建时间：2019/08/24 12:32 <br/>
 */
public class CommonUtils {

    public static final CommonUtils Instance = new CommonUtils();

    /**
     * 休眠一段时间
     *
     * @param millis 毫秒
     */
    public static void sleep(long millis) throws InterruptedException {
        Thread.sleep(millis);
    }
}
