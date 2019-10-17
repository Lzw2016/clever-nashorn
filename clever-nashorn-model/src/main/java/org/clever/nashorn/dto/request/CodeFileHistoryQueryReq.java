package org.clever.nashorn.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.clever.common.model.request.QueryByPage;

@EqualsAndHashCode(callSuper = true)
@Data
public class CodeFileHistoryQueryReq extends QueryByPage {
    @ApiModelProperty("业务类型")
    private String bizType;

    @ApiModelProperty("代码分组")
    private String groupName;

    @ApiModelProperty("文件全路径")
    private String fullPath;
}
