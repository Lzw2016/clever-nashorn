package org.clever.nashorn.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.clever.common.model.request.BaseRequest;

import javax.validation.constraints.NotBlank;

@EqualsAndHashCode(callSuper = true)
@Data
public class FileNameHistoryReq extends BaseRequest {
    @ApiModelProperty("业务类型")
    @NotBlank
    private String bizType;

    @ApiModelProperty("代码分组")
    @NotBlank
    private String groupName;

    @ApiModelProperty("文件路径")
    @NotBlank
    private String filePath;
}
