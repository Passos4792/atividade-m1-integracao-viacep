package com.example.crud.infra;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
@Configuration
public class ViaCepConfig {
    @Bean
    public RestTemplate viaCepRestTemplate(
            @Value("${viacep.connect-timeout-ms:2000}") int connectTimeout,
            @Value("${viacep.read-timeout-ms:3000}") int readTimeout) {
        var factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(connectTimeout); factory.setReadTimeout(readTimeout);
        return new RestTemplate(factory);
    }
}
