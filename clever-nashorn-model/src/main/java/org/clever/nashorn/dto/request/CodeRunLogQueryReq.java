package org.clever.nashorn.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.clever.common.model.request.QueryByPage;

import java.util.Date;

/**
 * 作者：lizw <br/>
 * 创建时间：2019/10/18 11:56 <br/>
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CodeRunLogQueryReq extends QueryByPage {

    @ApiModelProperty("业务类型")
    private String bizType;

    @ApiModelProperty("代码分组")
    private String groupName;

    @ApiModelProperty("文件ID(js_code_file.id)")
    private Long fileId;

    @ApiModelProperty("文件全路径")
    private String fullPath;

    @ApiModelProperty("运行开始时间-开始")
    private Date runStartStart;

    @ApiModelProperty("运行开始时间-结束")
    private Date runStartEnd;

    @ApiModelProperty("运行结束时间-开始")
    private Date runEndStart;

    @ApiModelProperty("运行结束时间-结束")
    private Date runEndEnd;

    @ApiModelProperty("脚本运行状态：1-运行中，2-成功，3-异常，4-超时，...")
    private Integer status;
}
