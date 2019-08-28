package org.clever.nashorn.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * JS代码运行日志
 *
 * @author lizw
 * @since 2019-08-27 09:24:01
 */
@Data
public class CodeRunLog implements Serializable {
    private static final long serialVersionUID = 955873015192527747L;
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
}