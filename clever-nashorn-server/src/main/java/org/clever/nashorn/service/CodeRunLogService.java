package org.clever.nashorn.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.clever.common.model.request.QueryBySort;
import org.clever.nashorn.dto.request.CodeRunLogQueryReq;
import org.clever.nashorn.dto.response.CodeRunLogQueryRes;
import org.clever.nashorn.entity.CodeRunLog;
import org.clever.nashorn.mapper.CodeRunLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 作者：lizw <br/>
 * 创建时间：2019/08/27 09:34 <br/>
 */
@Transactional(readOnly = true)
@Service
public class CodeRunLogService {
    @Autowired
    private CodeRunLogMapper codeRunLogMapper;

    public CodeRunLog getCodeRunLog(Long id) {
        return codeRunLogMapper.selectById(id);
    }

    public IPage<CodeRunLogQueryRes> queryByPage(CodeRunLogQueryReq query) {
        query.addOrderFieldMapping("id", "a.id");
        query.addOrderFieldMapping("jsCodeId", "a.js_code_id");
        query.addOrderFieldMapping("runStart", "a.run_start");
        query.addOrderFieldMapping("runEnd", "a.run_end");
        query.addOrderFieldMapping("status", "a.status");
        query.addOrderFieldMapping("createAt", "a.create_at");
        query.addOrderFieldMapping("updateAt", "a.update_at");
        query.addOrderFieldMapping("bizType", "b.biz_type");
        query.addOrderFieldMapping("groupName", "b.group_name");
        query.addOrderFieldMapping("filePath", "b.file_path");
        query.addOrderFieldMapping("name", "b.name");
        if (query.getOrderFields().size() <= 0) {
            query.addOrderField("createAt", QueryBySort.DESC);
        }
        return query.result(codeRunLogMapper.queryByPage(query));
    }
}
