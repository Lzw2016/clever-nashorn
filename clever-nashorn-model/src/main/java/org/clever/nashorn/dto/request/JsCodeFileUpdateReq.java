package org.clever.nashorn.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.clever.common.PatternConstant;
import org.clever.common.model.request.BaseRequest;
import org.clever.common.validation.StringNotBlank;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Pattern;

/**
 * 作者：lizw <br/>
 * 创建时间：2019/08/27 14:04 <br/>
 */
@ApiModel("更新JS代码或文件夹")
@EqualsAndHashCode(callSuper = true)
@Data
public class JsCodeFileUpdateReq extends BaseRequest {

    @ApiModelProperty("上级路径，以“/”号结尾")
    @StringNotBlank
    @Length(max = 255)
    private String filePath;

    @ApiModelProperty("文件或文件夹名称")
    @StringNotBlank
    @Pattern(regexp = PatternConstant.Name_Pattern + "{0,255}")
    private String name;

    @ApiModelProperty("脚本内容")
    private String jsCode;

    @ApiModelProperty("说明")
    @Length(max = 511)
    private String description;
}
