package org.clever.nashorn.config;

import com.google.gson.Gson;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.springframework.boot.autoconfigure.elasticsearch.jest.HttpClientConfigBuilderCustomizer;
import org.springframework.boot.autoconfigure.elasticsearch.jest.JestProperties;
import org.springframework.util.Assert;

import java.util.List;

/**
 * 作者：lizw <br/>
 * 创建时间：2019/10/23 16:08 <br/>
 */
public class JestClientBuilder {

    private final JestProperties properties;
    private final Gson gson;
    private final List<HttpClientConfigBuilderCustomizer> builderCustomizers;
    private JestClient jestClient;

    public JestClientBuilder(JestProperties properties, Gson gson, List<HttpClientConfigBuilderCustomizer> builderCustomizers) {
        this.properties = properties;
        this.gson = gson;
        this.builderCustomizers = builderCustomizers;
    }

    public synchronized JestClient builder() {
        if (jestClient == null) {
            JestClientFactory factory = new JestClientFactory();
            factory.setHttpClientConfig(createHttpClientConfig());
            jestClient = factory.getObject();
        }
        return jestClient;
    }

    private HttpClientConfig createHttpClientConfig() {
        HttpClientConfig.Builder builder = new HttpClientConfig.Builder(properties.getUris());
        if (StringUtils.isNotBlank(properties.getUsername())) {
            builder.defaultCredentials(properties.getUsername(), properties.getPassword());
        }
        JestProperties.Proxy proxy = this.properties.getProxy();
        if (StringUtils.isNotBlank(proxy.getHost())) {
            Assert.notNull(proxy.getPort(), "Proxy port must not be null");
            builder.proxy(new HttpHost(proxy.getHost(), proxy.getPort()));
        }
        if (gson != null) {
            builder.gson(gson);
        }
        if (properties.isMultiThreaded()) {
            builder.multiThreaded(true);
        }
        if (properties.getConnectionTimeout() != null) {
            builder.connTimeout((int) properties.getConnectionTimeout().toMillis());
        }
        if (properties.getReadTimeout() != null) {
            builder.readTimeout((int) properties.getReadTimeout().toMillis());
        }
        customize(builder);
        return builder.build();
    }

    private void customize(HttpClientConfig.Builder builder) {
        if (builderCustomizers == null) {
            return;
        }
        builderCustomizers.forEach((customizer) -> customizer.customize(builder));
    }
}
