package org.clever.nashorn.dto.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.clever.nashorn.model.WebSocketTaskReq;

import javax.validation.constraints.NotBlank;

/**
 * 请求 Debug JS
 * 作者：lizw <br/>
 * 创建时间：2019/08/21 17:42 <br/>
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DebugReq extends WebSocketTaskReq {
    /**
     * 业务类型
     */
    @NotBlank
    private final String bizType;
    /**
     * 代码分组
     */
    @NotBlank
    private final String groupName;
    /**
     * 调用文件名(全路径)
     */
    @NotBlank
    private String fileFullPath;

    /**
     * 调用方法
     */
    @NotBlank
    private String fucName;

//    @NotBlank
//    private String jsCode;
}
