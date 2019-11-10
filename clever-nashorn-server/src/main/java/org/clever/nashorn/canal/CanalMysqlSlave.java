package org.clever.nashorn.canal;

import lombok.extern.slf4j.Slf4j;
import org.clever.canal.instance.core.CanalInstanceGenerator;
import org.clever.canal.instance.manager.CanalConfigClient;
import org.clever.canal.instance.manager.ManagerCanalInstanceGenerator;
import org.clever.canal.instance.manager.model.Canal;
import org.clever.canal.instance.manager.model.CanalParameter;
import org.clever.canal.instance.manager.model.DataSourcing;
import org.clever.canal.instance.manager.model.SourcingType;
import org.clever.canal.server.embedded.CanalServerWithEmbedded;
import org.clever.nashorn.ScriptModuleInstance;
import org.clever.nashorn.config.CanalConfig;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Mysql Slave 的 Canal 实现
 */
@Slf4j
public class CanalMysqlSlave {
    private static final CanalServerWithEmbedded Canal_Server_With_Embedded = CanalServerWithEmbedded.Instance;
    private static final AtomicLong Canal_Id = new AtomicLong(0);
    private static final AtomicInteger Client_Id = new AtomicInteger(0);
    /**
     * Script 实例
     */
    private final ScriptModuleInstance scriptModuleInstance;
    /**
     * Canal配置集合(destination --> CanalConfig)
     */
    private final Map<String, CanalConfig> canalConfigMap;
    /**
     * 消费线程
     */
    private final Map<String, ConsumeBinlogThread> consumeBinlogThreadHashMap = new HashMap<>();

    public CanalMysqlSlave(ScriptModuleInstance scriptModuleInstance, Map<String, CanalConfig> canalConfigMap) {
        this.scriptModuleInstance = scriptModuleInstance;
        this.canalConfigMap = canalConfigMap;
        CanalConfigClient canalConfigClient = new CanalConfigClient() {

            @Override
            public Canal findCanal(String destination) {
                CanalConfig canalConfig = canalConfigMap.get(destination);
                if (canalConfig == null) {
                    throw new RuntimeException(String.format("destination=[%s] 配置不存在", destination));
                }
                // TODO 数据库配置需要优化
                CanalParameter canalParameter = canalConfig.getCanalParameter();
                canalParameter.addGroupDbAddresses(new DataSourcing(SourcingType.MYSQL, new InetSocketAddress(canalConfig.getHostname(), canalConfig.getPort())));
                return new Canal(Canal_Id.incrementAndGet(), destination, canalConfig.getCanalParameter());
            }

            @Override
            public String findFilter(String destination) {
                CanalConfig canalConfig = canalConfigMap.get(destination);
                if (canalConfig == null) {
                    throw new RuntimeException(String.format("destination=[%s] 配置不存在", destination));
                }
                return canalConfig.getFilter();
            }
        };
        // 创建Canal Instance 的实现
        CanalInstanceGenerator canalInstanceGenerator = new ManagerCanalInstanceGenerator(canalConfigClient);
        Canal_Server_With_Embedded.setCanalInstanceGenerator(canalInstanceGenerator);
    }

    public void start() {
        if (!Canal_Server_With_Embedded.isStart()) {
            Canal_Server_With_Embedded.start();
        }
        canalConfigMap.keySet().forEach(destination -> {
            if (!Canal_Server_With_Embedded.isStart(destination)) {
                Canal_Server_With_Embedded.start(destination);
                subscribe(destination);
            }
        });
    }

    public void stop() {
        canalConfigMap.keySet().forEach(destination -> {
            unsubscribe(destination);
            Canal_Server_With_Embedded.stop(destination);
        });
        Canal_Server_With_Embedded.stop();
    }

    private void subscribe(String destination) {
        if (consumeBinlogThreadHashMap.containsKey(destination)) {
            return;
        }
        ConsumeBinlogThread consumeBinlogThread = new ConsumeBinlogThread(Canal_Server_With_Embedded, scriptModuleInstance, (short) Client_Id.incrementAndGet(), destination);
        consumeBinlogThreadHashMap.put(destination, consumeBinlogThread);
        Canal_Server_With_Embedded.subscribe(consumeBinlogThread.getClientIdentity());
        consumeBinlogThread.start();
        log.info("### [subscribe] [{}]-[{}] 监听成功！", destination, consumeBinlogThread.getClientIdentity().getClientId());
    }

    private void unsubscribe(String destination) {
        ConsumeBinlogThread consumeBinlogThread = consumeBinlogThreadHashMap.get(destination);
        if (consumeBinlogThread == null) {
            return;
        }
        // Canal_Server_With_Embedded.unsubscribe(consumeBinlogThread.getClientIdentity());
        consumeBinlogThread.setRunning(false);
        final long start = System.currentTimeMillis();
        final long timeOut = 1000 * 3;
        while (consumeBinlogThread.isAlive() && (System.currentTimeMillis() - start) <= timeOut) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {
            }
        }
        consumeBinlogThreadHashMap.remove(destination);
        log.info("### [unsubscribe] [{}]-[{}] 取消监听成功！", destination, consumeBinlogThread.getClientIdentity().getClientId());
    }
}
