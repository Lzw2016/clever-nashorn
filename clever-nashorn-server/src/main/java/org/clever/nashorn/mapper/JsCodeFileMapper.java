package org.clever.nashorn.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.clever.nashorn.entity.JsCodeFile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * 作者：lizw <br/>
 * 创建时间：2019/08/27 09:32 <br/>
 */
@Mapper
@Repository
public interface JsCodeFileMapper extends BaseMapper<JsCodeFile> {

    /**
     * 获取单个 JsCodeFile
     *
     * @param bizType   业务类型
     * @param groupName 代码分组
     * @param nodeType  数据类型
     * @param fullPath  全路径 filePath(上级路径) + name(文件或文件夹名称)
     */
    @Select("select * from js_code_file where biz_type=#{bizType} and group_name=#{groupName} and node_type=#{nodeType} and CONCAT(file_path, name)=#{fullPath} limit 1")
    JsCodeFile getJsCodeFile(@Param("bizType") String bizType,
                             @Param("groupName") String groupName,
                             @Param("nodeType") int nodeType,
                             @Param("fullPath") String fullPath);

    /**
     * 获取单个 JsCodeFile
     *
     * @param bizType   业务类型
     * @param groupName 代码分组
     * @param fullPath  全路径 filePath(上级路径) + name(文件或文件夹名称)
     */
    @Select("select * from js_code_file where biz_type=#{bizType} and group_name=#{groupName} and CONCAT(file_path,name)=#{fullPath} limit 1")
    JsCodeFile getByFullPath(@Param("bizType") String bizType, @Param("groupName") String groupName, @Param("fullPath") String fullPath);

    @Select("select * from js_code_file")
    Set<JsCodeFile> findAll();

    @Select("select * from js_code_file where biz_type=#{bizType} and group_name=#{groupName}")
    List<JsCodeFile> findByBizAndGroup(@Param("bizType") String bizType, @Param("groupName") String groupName);

    @Select("select count(1) from js_code_file where biz_type=#{bizType} and group_name=#{groupName}")
    long countByBizAndGroup(@Param("bizType") String bizType, @Param("groupName") String groupName);
}
