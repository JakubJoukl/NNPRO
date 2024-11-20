package com.example.nnprorocnikovyprojekt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

@SpringBootApplication
public class NnproRocnikovyProjektApplication {

    public static void main(String[] args) {
        SpringApplication.run(NnproRocnikovyProjektApplication.class, args);
    }

    //https://github.com/FasterXML/jackson-modules-java8/issues/11#issuecomment-913199874
    @Autowired
    private RequestMappingHandlerAdapter handlerAdapter;
    //
    @EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) {
        handlerAdapter
                .getMessageConverters()
                .stream()
                .forEach(c -> {
                    if (c instanceof MappingJackson2HttpMessageConverter) {
                        MappingJackson2HttpMessageConverter jsonMessageConverter = (MappingJackson2HttpMessageConverter) c;
                        ObjectMapper objectMapper = jsonMessageConverter.getObjectMapper();
                        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                    }
                });
    }
}
