package org.clever.nashorn.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.clever.common.PatternConstant;
import org.clever.common.model.request.BaseRequest;
import org.clever.common.validation.ValidIntegerStatus;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * 作者：lizw <br/>
 * 创建时间：2019/08/27 13:03 <br/>
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class JsCodeFileAddReq extends BaseRequest {

    /**
     * 业务类型
     */
    @ApiModelProperty()
    @NotBlank
    private String bizType;

    /**
     * 代码分组
     */
    private String groupName;

    /**
     * 数据类型：1-文件，2-文件夹
     */
    @ValidIntegerStatus(value = {1, 2}, message = "1-文件，2-文件夹")
    private Integer nodeType;

    /**
     * 上级路径
     */
    private String filePath;

    /**
     * 文件或文件夹名称
     */
    @Pattern(regexp = PatternConstant.Name_Pattern + "{0,255}")
    private String name;

    /**
     * 脚本内容
     */
    private String jsCode;

    /**
     * 说明
     */
    private String description;
}
