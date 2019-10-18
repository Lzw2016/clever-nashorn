package org.clever.nashorn.dto.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.clever.common.model.response.BaseResponse;
import org.clever.nashorn.model.TaskInfo;

import java.util.Collections;
import java.util.List;

/**
 * 作者：lizw <br/>
 * 创建时间：2019/10/18 10:12 <br/>
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TaskManagerRes extends BaseResponse {
    @ApiModelProperty("任务摘要信息(文本)")
    private String summaryText;

    @ApiModelProperty("WebSocket连接总数")
    private Integer allWebSocketSessionSize;

    @ApiModelProperty("任务总数")
    private Integer taskCount;

    @ApiModelProperty("删除任务总数")
    private Integer removeTaskCount;

    @ApiModelProperty("任务线程池CorePoolSize总数")
    private Integer sumCorePoolSize;

    @ApiModelProperty("任务线程池MaximumPoolSize总数")
    private Integer sumMaximumPoolSize;

    @ApiModelProperty("任务线程池PoolSize总数")
    private Integer sumPoolSize;

    @ApiModelProperty("任务线程池QueueSize总数")
    private Integer sumQueueSize;

    @ApiModelProperty("任务线程池TaskCount总数")
    private Integer sumTaskCount;

    @ApiModelProperty("任务线程池ActiveCount总数")
    private Integer sumActiveCount;

    @ApiModelProperty("任务线程池CompletedTaskCount总数")
    private Integer sumCompletedTaskCount;

    @ApiModelProperty("任务详情信息")
    public List<TaskInfo> taskInfoList = Collections.emptyList();
}
