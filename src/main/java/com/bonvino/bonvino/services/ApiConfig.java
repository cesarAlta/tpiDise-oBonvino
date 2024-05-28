package com.bonvino.bonvino.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

//Creamos un bean que con el decorador configuracion se inicia con el proyecto y esta listo para ser utilizado
//Lo anotamos con un bean que nos queda disponnible para inyectarlo
@Configuration
public class ApiConfig {
    //    crea un resTemplate y jackson es encargado del mapeo , por defecto. Y devuelve el restemaple.
    @Bean
    RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(new ObjectMapper());
        restTemplate.getMessageConverters().add(converter);
        return restTemplate;

    }
}
