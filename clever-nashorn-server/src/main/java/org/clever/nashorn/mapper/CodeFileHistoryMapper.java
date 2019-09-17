package org.clever.nashorn.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.clever.nashorn.entity.CodeFileHistory;
import org.springframework.stereotype.Repository;

/**
 * 作者：lizw <br/>
 * 创建时间：2019/08/27 09:29 <br/>
 */
@Mapper
@Repository
public interface CodeFileHistoryMapper extends BaseMapper<CodeFileHistory> {

    @Select("select * from code_file_history where biz_type=#{bizType} and group_name=#{groupName} and file_path=#{filePath} and name=#{name} order by create_at desc limit 1")
    CodeFileHistory getLastHistory(@Param("bizType") String bizType, @Param("groupName") String groupName, @Param("filePath") String filePath, @Param("name") String name);
}
