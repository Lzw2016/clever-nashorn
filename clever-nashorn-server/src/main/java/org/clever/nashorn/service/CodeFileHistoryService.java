package org.clever.nashorn.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.clever.common.model.request.QueryBySort;
import org.clever.nashorn.dto.request.CodeFileHistoryQueryReq;
import org.clever.nashorn.dto.request.FileNameHistoryReq;
import org.clever.nashorn.dto.request.RevertFileReq;
import org.clever.nashorn.entity.CodeFileHistory;
import org.clever.nashorn.entity.JsCodeFile;
import org.clever.nashorn.mapper.CodeFileHistoryMapper;
import org.clever.nashorn.mapper.JsCodeFileMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * 作者：lizw <br/>
 * 创建时间：2019/08/27 09:33 <br/>
 */
@Transactional(readOnly = true)
@Service
public class CodeFileHistoryService {
    @Autowired
    private CodeFileHistoryMapper codeFileHistoryMapper;
    @Autowired
    private JsCodeFileMapper jsCodeFileMapper;

    public CodeFileHistory getCodeFileHistory(Long id) {
        return codeFileHistoryMapper.selectById(id);
    }

    public IPage<CodeFileHistory> queryByPage(CodeFileHistoryQueryReq query) {
        query.addOrderFieldMapping("bizType", "biz_type");
        query.addOrderFieldMapping("groupName", "group_name");
        query.addOrderFieldMapping("filePath", "file_path");
        query.addOrderFieldMapping("name", "name");
        query.addOrderFieldMapping("createAt", "create_at");
        query.addOrderFieldMapping("updateAt", "update_at");
        if (query.getOrderFields().size() <= 0) {
            query.addOrderField("createAt", QueryBySort.DESC);
        }
        return query.result(codeFileHistoryMapper.queryByPage(query));
    }

    public List<String> fileName(FileNameHistoryReq query) {
        return codeFileHistoryMapper.fileName(query);
    }

    @Transactional
    public JsCodeFile revertFile(RevertFileReq req) {
        JsCodeFile jsCodeFile = jsCodeFileMapper.selectById(req.getFileId());
        if (jsCodeFile == null) {
            throw new RuntimeException("文件不存在");
        }
        CodeFileHistory codeFileHistory = codeFileHistoryMapper.selectById(req.getHistoryFileId());
        if (codeFileHistory == null) {
            throw new RuntimeException("文件历史不存在");
        }
        if (!Objects.equals(jsCodeFile.getBizType(), codeFileHistory.getBizType())
                || !Objects.equals(jsCodeFile.getGroupName(), codeFileHistory.getGroupName())
                || !Objects.equals(jsCodeFile.getFilePath(), codeFileHistory.getFilePath())
                || !Objects.equals(jsCodeFile.getName(), codeFileHistory.getName())) {
            throw new RuntimeException("当前文件与历史文件不匹配");
        }
        JsCodeFile update = new JsCodeFile();
        update.setId(jsCodeFile.getId());
        update.setJsCode(codeFileHistory.getJsCode());
        jsCodeFileMapper.updateById(update);
        return jsCodeFileMapper.selectById(jsCodeFile.getId());
    }
}
