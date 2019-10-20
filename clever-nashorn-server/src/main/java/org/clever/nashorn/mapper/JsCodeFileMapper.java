package org.clever.nashorn.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.clever.nashorn.entity.JsCodeFile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
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
     * @param filePath  上级路径
     * @param name      文件或文件夹名称
     */
    @Select({
            "select * from js_code_file ",
            "where biz_type=#{bizType} and group_name=#{groupName} and node_type=#{nodeType} and file_path=#{filePath} and name=#{name} ",
            "limit 1"
    })
    JsCodeFile getJsCodeFile(@Param("bizType") String bizType,
                             @Param("groupName") String groupName,
                             @Param("nodeType") int nodeType,
                             @Param("filePath") String filePath,
                             @Param("name") String name);

    /**
     * 获取单个 JsCodeFile
     *
     * @param bizType   业务类型
     * @param groupName 代码分组
     * @param filePath  上级路径
     * @param name      文件或文件夹名称
     */
    @Select({
            "select * from js_code_file ",
            "where biz_type=#{bizType} and group_name=#{groupName} and file_path=#{filePath} and name=#{name} ",
            "limit 1"
    })
    JsCodeFile getByFullPath(
            @Param("bizType") String bizType,
            @Param("groupName") String groupName,
            @Param("filePath") String filePath,
            @Param("name") String name);

    @Select("select * from js_code_file")
    Set<JsCodeFile> findAll();

    @Select({
            "select id, biz_type, group_name, node_type, read_only, disable_delete, file_path, name, description, create_at, update_at from js_code_file ",
            "where biz_type=#{bizType} and group_name=#{groupName} "
    })
    List<JsCodeFile> findByBizAndGroup(@Param("bizType") String bizType, @Param("groupName") String groupName);

    @Select({
            "select id, biz_type, group_name, node_type, read_only, disable_delete, file_path, name, description, create_at, update_at from js_code_file ",
            "where file_path like concat(#{filePath}, '%')"
    })
    List<JsCodeFile> findAllChildByFilePath(@Param("filePath") String filePath);

    @Select("select distinct biz_type, group_name from js_code_file")
    List<Map<String, String>> allBizType();

    @Select("select distinct biz_type from js_code_file")
    List<String> bizTypeList();

    @Select("select distinct group_name from js_code_file where biz_type=#{bizType}")
    List<String> allGroupName(@Param("bizType") String bizType);

    @Select("select count(1) from js_code_file where biz_type=#{bizType} and group_name=#{groupName}")
    long countByBizAndGroup(@Param("bizType") String bizType, @Param("groupName") String groupName);
}
