package org.clever.nashorn.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * WebSocket 任务请求数据
 * <p>
 * 作者：lizw <br/>
 * 创建时间：2019/08/24 12:28 <br/>
 */
@Data
public abstract class WebSocketTaskReq implements Serializable {

    /**
     * 常规数据交互
     */
    public static final String Type_Normal = "normal";
    /**
     * 加入到某个任务中
     */
    public static final String Type_Join_Task = "join_task";

    /**
     * 请求数据类型
     */
    @NotBlank
    private String type;
    /**
     * 任务ID
     */
    private String taskId;
}
