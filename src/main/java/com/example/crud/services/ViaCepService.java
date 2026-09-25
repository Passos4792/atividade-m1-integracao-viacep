package com.example.crud.services;
import com.example.crud.infra.ApiException;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.*;
import java.net.SocketTimeoutException;
import java.text.Normalizer;
import java.util.Locale;
@Service
public class ViaCepService {
    private final RestTemplate client;
    private final ProductService products;
    private final String baseUrl;
    public ViaCepService(RestTemplate viaCepRestTemplate, ProductService products,
            @Value("${viacep.base-url:https://viacep.com.br/ws}") String baseUrl) {
        this.client = viaCepRestTemplate; this.products = products;
        this.baseUrl = baseUrl.replaceAll("/+$", "");
    }
    public boolean isAvailable(String productId, String cep) {
        if (cep == null || !cep.matches("[0-9]{5}-?[0-9]{3}"))
            throw new ApiException(HttpStatus.BAD_REQUEST,"CEP deve conter 8 dígitos, com hífen opcional");
        var product = products.findActive(productId);
        Address address;
        try {
            address = client.getForObject(baseUrl + "/{cep}/json/", Address.class,cep.replace("-",""));
        } catch (ResourceAccessException ex) {
            Throwable cause = ex;
            while (cause != null) {
                if (cause instanceof SocketTimeoutException)
                    throw new ApiException(HttpStatus.GATEWAY_TIMEOUT,"Tempo limite na consulta à ViaCEP");
                cause = cause.getCause();
            }
            throw new ApiException(HttpStatus.SERVICE_UNAVAILABLE,"ViaCEP indisponível; tente novamente mais tarde");
        } catch (RestClientResponseException ex) {
            throw new ApiException(HttpStatus.SERVICE_UNAVAILABLE,"ViaCEP indisponível; tente novamente mais tarde");
        } catch (RestClientException ex) {
            throw new ApiException(HttpStatus.BAD_GATEWAY,"Resposta inválida da ViaCEP");
        }
        if (address != null && Boolean.TRUE.equals(address.erro()))
            throw new ApiException(HttpStatus.NOT_FOUND,"CEP não encontrado");
        if (address == null || address.localidade() == null || address.localidade().isBlank())
            throw new ApiException(HttpStatus.BAD_GATEWAY,"Resposta inválida da ViaCEP");
        return normalize(address.localidade()).equals(normalize(product.getDistributionCenter()));
    }
    private String normalize(String value) {
        return Normalizer.normalize(value,Normalizer.Form.NFD).replaceAll("\\p{M}","")
            .strip().replaceAll("\\s+"," ").toLowerCase(Locale.ROOT);
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Address(String localidade, Boolean erro) {}
}
