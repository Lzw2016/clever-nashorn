package org.clever.nashorn.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * WebSocket 任务响应数据
 * <p>
 * 作者：lizw <br/>
 * 创建时间：2019/08/24 12:28 <br/>
 */
@Data
public abstract class WebSocketTaskRes implements Serializable {
    public static final String Level_Log = "log";
    public static final String Level_Trace = "trace";
    public static final String Level_Debug = "debug";
    public static final String Level_Info = "info";
    public static final String Level_Warn = "warn";
    public static final String Level_Error = "error";

    /**
     * 返回数据
     */
    public static final String Type_Data = "data";
    /**
     * 返回日志
     */
    public static final String Type_Log = "log";
    /**
     * 返回Console打印数据
     */
    public static final String Type_Console = "console";

    /**
     * 当前时间
     */
    private Date timestamp = new Date();
    /**
     * 响应数据类型
     */
    private String type;
}
