package org.clever.nashorn.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.clever.nashorn.dto.request.CodeRunLogQueryReq;
import org.clever.nashorn.dto.response.CodeRunLogQueryRes;
import org.clever.nashorn.entity.CodeRunLog;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 作者：lizw <br/>
 * 创建时间：2019/08/27 09:31 <br/>
 */
@Mapper
@Repository
public interface CodeRunLogMapper extends BaseMapper<CodeRunLog> {

    List<CodeRunLogQueryRes> queryByPage(@Param("query") CodeRunLogQueryReq query);
}
