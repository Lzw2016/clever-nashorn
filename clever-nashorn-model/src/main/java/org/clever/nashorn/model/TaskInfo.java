package org.clever.nashorn.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 作者：lizw <br/>
 * 创建时间：2019/10/18 10:11 <br/>
 */
@Data
public class TaskInfo implements Serializable {
    /**
     * 任务ID
     */
    private String taskId;
    /**
     * 任务类型
     */
    private String taskType;
    /**
     * WebSocket连接数
     */
    private Integer webSocketSessionSize;
    /**
     * 运行的任务数
     */
    private Long runningTaskCount;
    /**
     * 总任务数
     */
    private Long totalTaskCount;
}
