package org.clever.nashorn.dto.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.clever.nashorn.model.WebSocketTaskReq;

/**
 * 请求 Debug JS
 * 作者：lizw <br/>
 * 创建时间：2019/08/21 17:42 <br/>
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DebugReq extends WebSocketTaskReq {

    /**
     * 调用文件路径
     */
    private String filePath;

    /**
     * 调用文件名
     */
    private String fileName;

    /**
     * 调用方法
     */
    private String fucName;

//    @NotBlank
//    private String jsCode;
}
