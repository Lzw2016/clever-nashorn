package org.clever.nashorn.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.clever.nashorn.entity.CodeRunLog;
import org.springframework.stereotype.Repository;

/**
 * 作者：lizw <br/>
 * 创建时间：2019/08/27 09:31 <br/>
 */
@Mapper
@Repository
public interface CodeRunLogMapper extends BaseMapper<CodeRunLog> {
}
