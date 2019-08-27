package org.clever.nashorn.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.clever.common.model.request.BaseRequest;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * 作者：lizw <br/>
 * 创建时间：2019/08/27 14:24 <br/>
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class JsCodeFileTreeFindReq extends BaseRequest {

    @ApiModelProperty("业务类型")
    @NotBlank
    @Length(max = 127)
    private String bizType;

    @ApiModelProperty("代码分组")
    @NotBlank
    @Length(max = 127)
    private String groupName;
}
