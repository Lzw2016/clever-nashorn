package org.clever.nashorn.dto.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.clever.common.model.response.BaseResponse;

import java.util.Date;

/**
 * 作者：lizw <br/>
 * 创建时间：2019/10/18 12:50 <br/>
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CodeRunLogQueryRes extends BaseResponse {

    /** 主键id */
    private Long id;

    /** 系统JS代码ID(js_code_file.id) */
    private Long jsCodeId;

    /** 脚本内容 */
    private String jsCode;

    /** 运行开始时间 */
    private Date runStart;

    /** 运行结束时间 */
    private Date runEnd;

    /** 运行日志 */
    private String runLog;

    /** 脚本运行状态：1-运行中，2-成功，3-异常，4-超时，... */
    private Integer status;

    /** 创建时间 */
    private Date createAt;

    /** 更新时间 */
    private Date updateAt;

    /** 业务类型 */
    private String bizType;

    /** 代码分组 */
    private String groupName;

    /** 上级路径，以“/”号结尾 */
    private String filePath;

    /** 文件或文件夹名称 */
    private String name;

    /**
     * 执行时间(单位:微秒)
     */
    private Long runTime;
}
