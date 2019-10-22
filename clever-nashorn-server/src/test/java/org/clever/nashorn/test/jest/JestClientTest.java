package org.clever.nashorn.test.jest;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.JestResult;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.core.Delete;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Index;
import io.searchbox.core.Update;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.DeleteIndex;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 作者：lizw <br/>
 * 创建时间：2019/10/22 13:08 <br/>
 */
@Slf4j
public class JestClientTest {
    private static final JestClientFactory factory;
    private static final JestClient client;

    static {
        factory = new JestClientFactory();
        factory.setHttpClientConfig(new HttpClientConfig
                .Builder("http://elasticsearch.msvc.top/")
                .multiThreaded(true)
                // 默认情况下，此实现将为每个给定路由创建不超过2个并发连接
                .defaultMaxTotalConnectionPerRoute(1)
                // 总共没有20个连接
                .maxTotalConnection(5)
                .build());
        client = factory.getObject();
    }

    @After
    public void close() throws IOException {
        client.close();
    }

    @Test
    public void t1() throws IOException {
        CreateIndex.Builder builder = new CreateIndex.Builder("lzw-test")
                .settings(new HashMap<String, Object>() {{
                    put("index", new HashMap<String, Object>() {{
                        put("number_of_shards", 1);
                        put("number_of_replicas", 0);
                    }});
                }});
        // Gson Bug导致不能成功(不能使用上面HashMap的初始化语法)
        JestResult jestResult = client.execute(builder.build());
        log.info("--> {}", jestResult.isSucceeded());
    }

    @Test
    public void t2() throws IOException {
        Map<String, Object> settings = new HashMap<>();
        Map<String, Object> index = new HashMap<>();
        index.put("number_of_shards", 1);
        index.put("number_of_replicas", 0);
        settings.put("index", index);
        CreateIndex.Builder builder = new CreateIndex.Builder("lzw-test")
                .settings(settings);
        // 可用成功
        JestResult jestResult = client.execute(builder.build());
        log.info("--> {}", jestResult.getJsonString());
    }

    @Test
    public void t3() throws IOException {
        DeleteIndex.Builder builder = new DeleteIndex.Builder("lzw-test");
        JestResult jestResult = client.execute(builder.build());
        log.info("--> {}", jestResult.getJsonString());
    }

    @Test
    public void t5() throws IOException {
        // 新增或者更新
        Map<String, Object> index = new HashMap<>();
        index.put("String", "sss");
        index.put("int", 123);
        index.put("double", 123.456);
        index.put("date", new Date());
        index.put("boolean", true);
        index.put("char", 'c');
        Index.Builder builder = new Index.Builder(index)
                .index("lzw-test")
                .type("test")
                .id("lzw" + UUID.randomUUID().toString());
        DocumentResult jestResult = client.execute(builder.build());
        log.info("--> {}", jestResult.getId());
    }

    @Test
    public void t6() throws IOException {
        // 更新单个字段
        String script = "{\n" +
                "    \"script\" : \"ctx._source.int += 1\"" +
                "}";
        Update.Builder builder = new Update.Builder(script)
                .index("lzw-test")
                .type("test")
                .id("lzw4274994c-1509-400e-ba78-996dad04688b");
        DocumentResult jestResult = client.execute(builder.build());
        log.info("--> {}", jestResult.getJsonObject());
    }

    @Test
    public void t7() throws IOException {
        Delete.Builder builder = new Delete.Builder("lzwdb4849e7-2254-44ec-81ee-b10319881fd9")
                .index("lzw-test")
                .type("test");
        DocumentResult jestResult = client.execute(builder.build());
        log.info("--> {}", jestResult.getId());
    }
}
