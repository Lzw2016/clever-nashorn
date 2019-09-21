package org.clever.nashorn.config;

import org.clever.nashorn.intercept.HttpRequestJsHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 作者： lzw<br/>
 * 创建时间：2019-05-17 14:29 <br/>
 */
@Configuration
public class ServerWebMvcConfigurer implements WebMvcConfigurer {

//    @Autowired
//    private GlobalConfig globalConfig;

//    /**
//     * 自定义静态资源访问映射
//     */
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("/api/excel-templates/**").addResourceLocations("classpath:/excel-templates/");
//    }

    @Autowired
    private HttpRequestJsHandler httpRequestJsHandler;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(httpRequestJsHandler).addPathPatterns("/**");
    }
}
