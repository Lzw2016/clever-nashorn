package org.clever.nashorn.dto.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.clever.nashorn.model.WebSocketTaskReq;

import javax.validation.constraints.NotBlank;

/**
 * 作者：lizw <br/>
 * 创建时间：2019/09/28 15:14 <br/>
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ListenerLogsReq extends WebSocketTaskReq {

    @NotBlank
    private final String bizType;
    @NotBlank
    private final String groupName;
    /**
     * 调用文件名(全路径)
     */
    @NotBlank
    private String fileFullPath;
}
