package io.leego.ah.openapi.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * @author Leego Yih
 */
public abstract class BaseServiceImpl {
    private static final Logger logger = LoggerFactory.getLogger(BaseServiceImpl.class);
    protected final ObjectMapper objectMapper;

    public BaseServiceImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public <T> T readValue(String s, Class<T> clazz) {
        if (!StringUtils.hasText(s)) {
            return null;
        }
        try {
            return objectMapper.readValue(s, clazz);
        } catch (IOException e) {
            logger.error("Failed to parse: {}", s);
        }
        return null;
    }

    public String writeValue(Object o) {
        if (o == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(o);
        } catch (Exception e) {
            logger.error("Failed to write: {}", o, e);
        }
        return null;
    }
}
