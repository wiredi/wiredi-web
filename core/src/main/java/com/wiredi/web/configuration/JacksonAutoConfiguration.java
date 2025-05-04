package com.wiredi.web.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wiredi.annotations.Provider;
import com.wiredi.annotations.stereotypes.AutoConfiguration;
import com.wiredi.runtime.domain.conditional.builtin.ConditionalOnBean;
import com.wiredi.web.response.messaging.JacksonJsonHttpContentConverter;

@AutoConfiguration
@ConditionalOnBean(type = ObjectMapper.class)
public class JacksonAutoConfiguration {

    @Provider
    public JacksonJsonHttpContentConverter jacksonHttpMessageConverter() {
        return new JacksonJsonHttpContentConverter();
    }
}
