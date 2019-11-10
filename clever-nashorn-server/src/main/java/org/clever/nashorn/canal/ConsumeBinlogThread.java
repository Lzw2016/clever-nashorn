package org.clever.nashorn.canal;

import com.google.protobuf.InvalidProtocolBufferException;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.clever.canal.protocol.CanalEntry;
import org.clever.canal.protocol.ClientIdentity;
import org.clever.canal.protocol.Message;
import org.clever.canal.server.embedded.CanalServerWithEmbedded;
import org.clever.common.utils.exception.ExceptionUtils;
import org.clever.common.utils.spring.SpringContextHolder;
import org.clever.nashorn.ScriptModuleInstance;
import org.clever.nashorn.entity.EnumConstant;
import org.clever.nashorn.mapper.JsCodeFileMapper;
import org.clever.nashorn.utils.JsCodeFilePathUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 消费binlog 线程
 */
@Getter
@Setter
@Slf4j
public class ConsumeBinlogThread extends Thread {
    private static final long Time_Interval = 1000 * 60 * 5;
    private static final String Consume_Method = "onChange";
    private static final String Filter_Name = "filter";

    private final JsCodeFileMapper jsCodeFileMapper;
    private final CanalServerWithEmbedded canalServerWithEmbedded;
    private final ScriptModuleInstance scriptModuleInstance;

    private final ClientIdentity clientIdentity;
    private boolean running = true;
    private long lastTime = 0;
    private List<String> allFileFullPath;

    public ConsumeBinlogThread(CanalServerWithEmbedded canalServerWithEmbedded, ScriptModuleInstance scriptModuleInstance, short id, String destination) {
        this.canalServerWithEmbedded = canalServerWithEmbedded;
        this.scriptModuleInstance = scriptModuleInstance;
        setName(String.format("ConsumeBinlogThread-[%s]-[%s]", destination, id));
        setDaemon(true);
        clientIdentity = new ClientIdentity(destination, id);
        jsCodeFileMapper = SpringContextHolder.getBean(JsCodeFileMapper.class);
    }

    @Override
    public void run() {
        final int batchSize = 1;
        while (running) {
            try {
                Message message = canalServerWithEmbedded.get(clientIdentity, batchSize, 1L, TimeUnit.SECONDS);
                if (message.getId() == -1) {
                    continue;
                }
                List<CanalEntry.Entry> entryList;
                if (message.isRaw()) {
                    entryList = message.getRawEntries().stream().map(rawEntry -> {
                        try {
                            return CanalEntry.Entry.parseFrom(rawEntry);
                        } catch (Throwable e) {
                            log.error("反序列化失败", e);
                            throw ExceptionUtils.unchecked(e);
                        }
                    }).collect(Collectors.toList());
                } else {
                    entryList = message.getEntries();
                }
                consume(entryList);
            } catch (Throwable e) {
                log.error("Binlog消费失败", e);
            }
        }
    }

    /**
     * 消费Binlog <br />
     * https://github.com/alibaba/canal/wiki/%E7%AE%80%E4%BB%8B
     */
    private void consume(List<CanalEntry.Entry> entryList) throws InvalidProtocolBufferException {
        if (entryList == null || entryList.isEmpty()) {
            return;
        }
        log.info("消费Binlog - {} | {} | size={}", clientIdentity.getClientId(), clientIdentity.getDestination(), entryList.size());
        // List<Map<String, Object>> dataList = new ArrayList<>(entryList.size());
        for (CanalEntry.Entry entry : entryList) {
            if (!CanalEntry.EntryType.ROW_DATA.equals(entry.getEntryType())) {
                continue;
            }
            String fullSchemaName = String.format("%s.%s", entry.getHeader().getSchemaName(), entry.getHeader().getTableName());
            List<ScriptObjectMirror> jsConsumes = getJsConsumes(fullSchemaName);
            if (jsConsumes.isEmpty()) {
                continue;
            }
            Map<String, Object> dataMap = new HashMap<>(5);
            // dataList.add(dataMap);
            dataMap.put("entryType", entry.getEntryType());
            Map<String, Object> header = new HashMap<>(6);
            header.put("logfileName", entry.getHeader().getLogfileName());
            header.put("logfileOffset", entry.getHeader().getLogfileOffset());
            header.put("executeTime", entry.getHeader().getExecuteTime());
            header.put("schemaName", entry.getHeader().getSchemaName());
            header.put("tableName", entry.getHeader().getTableName());
            header.put("eventType", entry.getHeader().getEventType());
            dataMap.put("header", header);
            CanalEntry.RowChange rowChange = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
            dataMap.put("isDdl", rowChange.getIsDdl());
            dataMap.put("sql", rowChange.getSql());
            if (rowChange.getRowDataList() == null || rowChange.getRowDataList().isEmpty()) {
                continue;
            }
            List<Map<String, Object>> beforeColumns = new ArrayList<>(rowChange.getRowDataList().size());
            List<Map<String, Object>> afterColumns = new ArrayList<>(rowChange.getRowDataList().size());
            Map<String, Object> rowDataList = new HashMap<>(2);
            rowDataList.put("beforeColumns", beforeColumns);
            rowDataList.put("afterColumns", afterColumns);
            dataMap.put("rowDataList", rowDataList);
            for (CanalEntry.RowData rowData : rowChange.getRowDataList()) {
                for (CanalEntry.Column column : rowData.getBeforeColumnsList()) {
                    beforeColumns.add(columnToMap(column));
                }
                for (CanalEntry.Column column : rowData.getAfterColumnsList()) {
                    afterColumns.add(columnToMap(column));
                }
            }
            // 遍历消费
            jsConsumes.forEach(consume -> {
                try {
                    consume.callMember(Consume_Method, dataMap);
                } catch (Throwable e) {
                    log.error("Binlog消费失败 - [{}]", fullSchemaName, e);
                }
            });
        }
    }

    private Map<String, Object> columnToMap(CanalEntry.Column column) {
        Map<String, Object> map = new HashMap<>(7);
        map.put("index", column.getIndex());
        map.put("sqlType", column.getSqlType());
        map.put("name", column.getName());
        map.put("isKey", column.getIsKey());
        map.put("updated", column.getUpdated());
        map.put("isNull", column.getIsNull());
        map.put("value", column.getValue());
        return map;
    }


    private List<ScriptObjectMirror> getJsConsumes(String fullSchemaName) {
        String destination = clientIdentity.getDestination();
        long nowTime = System.currentTimeMillis();
        if ((nowTime - lastTime) > Time_Interval) {
            allFileFullPath = jsCodeFileMapper.findAllChildByFilePath(EnumConstant.DefaultBizType, EnumConstant.DefaultGroupName, String.format("/%s/", destination))
                    .stream()
                    .filter(file -> file != null && Objects.equals(file.getNodeType(), EnumConstant.Node_Type_1))
                    .map(file -> JsCodeFilePathUtils.concat(file.getFilePath(), file.getName()))
                    .collect(Collectors.toList());
            lastTime = nowTime;
        }
        List<ScriptObjectMirror> jsConsumes = new ArrayList<>();
        for (String fileFullPath : allFileFullPath) {
            ScriptObjectMirror scriptObjectMirror;
            try {
                scriptObjectMirror = scriptModuleInstance.useJs(fileFullPath);
            } catch (Throwable e) {
                log.error("加载JS文件失败", e);
                continue;
            }
            Object filterObj = scriptObjectMirror.get(Filter_Name);
            if (!(filterObj instanceof String)) {
                continue;
            }

            Object handlerObject = scriptObjectMirror.getMember(Consume_Method);
            if (!(handlerObject instanceof ScriptObjectMirror)) {
                continue;
            }
            ScriptObjectMirror handlerFunction = (ScriptObjectMirror) handlerObject;
            if (!handlerFunction.isFunction()) {
                continue;
            }
            // 过滤
            String filter = (String) filterObj;
            if (!filter(fullSchemaName, filter)) {
                continue;
            }
            jsConsumes.add(scriptObjectMirror);
        }
        return jsConsumes;
    }

    private boolean filter(String fullSchemaName, String filter) {
        String[] names = filter.split(",");
        for (String name : names) {
            if (StringUtils.isBlank(name)) {
                continue;
            }
            name = StringUtils.trim(name);
            if (name.equalsIgnoreCase(fullSchemaName)) {
                return true;
            }
        }
        return false;
    }
}