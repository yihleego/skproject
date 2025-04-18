package io.leego.ah.openapi.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import io.leego.ah.openapi.converter.InstantConverter;
import io.leego.ah.openapi.converter.SortConverter;
import io.leego.ah.openapi.databind.SortModule;
import io.leego.ah.openapi.interceptor.SecurityHandlerInterceptor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Leego Yih
 */
@Configuration
@EnableJpaAuditing
@EnableConfigurationProperties({SecurityProperties.class, DataSyncProperties.class})
public class OpenApiConfiguration {

    @Configuration
    public static class WebMvcConfiguration implements WebMvcConfigurer {
        private final SecurityHandlerInterceptor securityHandlerInterceptor;

        public WebMvcConfiguration(SecurityHandlerInterceptor securityHandlerInterceptor) {
            this.securityHandlerInterceptor = securityHandlerInterceptor;
        }

        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            registry.addInterceptor(securityHandlerInterceptor)
                    .excludePathPatterns("/",
                            "/**/*.html",
                            "/**/*.js",
                            "/**/*.css",
                            "/**/*.png",
                            "/**/*.json",
                            "/v3/api-docs");
        }

        @Override
        public void addCorsMappings(CorsRegistry registry) {
            registry.addMapping("/**")
                    .allowedOriginPatterns("*")
                    .allowCredentials(true)
                    .allowedMethods("*");
        }
    }

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        return JsonMapper.builder()
                .enable(MapperFeature.USE_GETTERS_AS_SETTERS)
                .enable(MapperFeature.REQUIRE_SETTERS_FOR_GETTERS)
                .enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES)
                .enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES)
                .enable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN)
                .enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .disable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .disable(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .serializationInclusion(JsonInclude.Include.NON_NULL)
                .addModules(new ParameterNamesModule(), new Jdk8Module(), new JavaTimeModule(), new SortModule())
                .build();
    }

    @Bean
    public InstantConverter instantConverter() {
        return new InstantConverter();
    }

    @Bean
    public SortConverter sortConverter() {
        return new SortConverter();
    }
}
