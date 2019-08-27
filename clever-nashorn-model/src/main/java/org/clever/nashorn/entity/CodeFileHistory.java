package org.clever.nashorn.entity;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * 历史JS代码
 *
 * @author lizw
 * @since 2019-08-27 09:24:01
 */
@Data
public class CodeFileHistory implements Serializable {
    private static final long serialVersionUID = -96652038065828113L;
    /** 主键id */    
    private Long id;
    
    /** 业务类型 */    
    private String bizType;
    
    /** 代码分组 */    
    private String groupName;
    
    /** 上级路径 */    
    private String filePath;
    
    /** 文件或文件夹名称 */    
    private String name;
    
    /** 脚本内容 */    
    private String jsCode;
    
    /** 说明 */    
    private String description;
    
    /** 创建时间 */    
    private Date createAt;
    
    /** 更新时间 */    
    private Date updateAt;
}