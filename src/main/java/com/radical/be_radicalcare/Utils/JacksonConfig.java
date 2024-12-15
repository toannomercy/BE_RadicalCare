package com.radical.be_radicalcare.Utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.registerModule(new Hibernate6Module()); // Xử lý proxy Hibernate
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false); // Ngăn lỗi "Empty Beans"
        return objectMapper;
    }
}
