package org.clever.nashorn.canal;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.clever.canal.protocol.CanalEntry;
import org.clever.canal.protocol.ClientIdentity;
import org.clever.canal.protocol.Message;
import org.clever.canal.server.embedded.CanalServerWithEmbedded;
import org.clever.common.utils.exception.ExceptionUtils;
import org.clever.nashorn.ScriptModuleInstance;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 消费binlog 线程
 */
@Getter
@Setter
@Slf4j
public class ConsumeBinlogThread extends Thread {
    private final CanalServerWithEmbedded canalServerWithEmbedded;
    /**
     * Script 实例
     */
    private final ScriptModuleInstance scriptModuleInstance;
    private final ClientIdentity clientIdentity;
    private boolean running = true;

    public ConsumeBinlogThread(CanalServerWithEmbedded canalServerWithEmbedded, ScriptModuleInstance scriptModuleInstance, short id, String destination) {
        this.canalServerWithEmbedded = canalServerWithEmbedded;
        this.scriptModuleInstance = scriptModuleInstance;
        clientIdentity = new ClientIdentity(destination, id);
        setName(String.format("ConsumeBinlogThread-[%s]-[%s]", destination, id));
        setDaemon(true);
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
                            log.info("反序列化失败", e);
                            throw ExceptionUtils.unchecked(e);
                        }
                    }).collect(Collectors.toList());
                } else {
                    entryList = message.getEntries();
                }
                consume(entryList);
            } catch (Throwable e) {
                log.info("Binlog消费失败", e);
            }
        }
    }

    /**
     * 消费Binlog
     */
    private void consume(List<CanalEntry.Entry> entryList) {
        log.info("消费Binlog - {} | {} | size={}", clientIdentity.getClientId(), clientIdentity.getDestination(), entryList.size());
    }
}
