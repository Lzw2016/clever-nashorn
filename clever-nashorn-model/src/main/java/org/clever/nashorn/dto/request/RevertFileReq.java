package org.clever.nashorn.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.clever.common.model.request.BaseRequest;

import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
public class RevertFileReq extends BaseRequest {
    @ApiModelProperty("文件ID")
    @NotNull
    private Long fileId;

    @ApiModelProperty("文件历史ID")
    @NotNull
    private Long historyFileId;
}
