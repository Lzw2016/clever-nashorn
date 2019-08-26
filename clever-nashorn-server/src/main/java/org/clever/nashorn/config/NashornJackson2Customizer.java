package org.clever.nashorn.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.internal.runtime.PropertyAccess;
import lombok.extern.slf4j.Slf4j;
import org.clever.nashorn.config.jackson.BindingsJsonSerializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import javax.script.Bindings;

/**
 * 作者：lizw <br/>
 * 创建时间：2019/08/26 17:44 <br/>
 */
@Configuration("NashornJackson2Customizer")
@ConditionalOnClass({Jackson2ObjectMapperBuilder.class, ObjectMapper.class})
@Slf4j
public class NashornJackson2Customizer implements Jackson2ObjectMapperBuilderCustomizer {

    @Override
    public void customize(Jackson2ObjectMapperBuilder jacksonObjectMapperBuilder) {
        // Bindings -> 序列化
        jacksonObjectMapperBuilder.serializerByType(JSObject.class, BindingsJsonSerializer.instance);
        jacksonObjectMapperBuilder.serializerByType(Bindings.class, BindingsJsonSerializer.instance);
        jacksonObjectMapperBuilder.serializerByType(PropertyAccess.class, BindingsJsonSerializer.instance);
        log.debug("### [jackson] JSObject|Bindings -> 序列化 -> BindingsJsonSerializer");
    }
}
