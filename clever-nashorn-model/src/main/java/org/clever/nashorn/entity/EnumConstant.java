package org.clever.nashorn.entity;

/**
 * 作者： lzw<br/>
 * 创建时间：2018-09-17 14:15 <br/>
 */
public class EnumConstant {

    /**
     * 文件路径统一分隔符
     */
    public static final String File_Path_Separator = "/";

    /**
     * 数据类型：1-文件
     */
    public static final Integer Node_Type_1 = 1;
    /**
     * 数据类型：2-文件夹
     */
    public static final Integer Node_Type_2 = 2;

    /**
     * 脚本运行状态：1-运行中
     */
    public static final Integer Run_Log_Status_1 = 1;
    /**
     * 脚本运行状态：2-成功
     */
    public static final Integer Run_Log_Status_2 = 2;
    /**
     * 脚本运行状态：3-异常
     */
    public static final Integer Run_Log_Status_3 = 3;
    /**
     * 脚本运行状态：4-超时，
     */
    public static final Integer Run_Log_Status_4 = 4;
}
