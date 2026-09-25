package com.example.crud;

import com.example.crud.domain.product.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import java.net.SocketTimeoutException;
import java.net.ConnectException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest @AutoConfigureMockMvc @ActiveProfiles("test") @Transactional
class ProductApiTests {
    @Autowired MockMvc mvc;
    @Autowired ProductRepository repository;
    @Autowired RestTemplate viaCepRestTemplate;
    @Autowired ObjectMapper mapper;
    MockRestServiceServer server;
    String id;
    @BeforeEach void setup() {
        server = MockRestServiceServer.bindTo(viaCepRestTemplate).build();
        id = repository.save(new Product(new RequestProduct(null,"Teste",1234,"teste","Mogi das Cruzes"))).getId();
    }
    @AfterEach void verifyRequests() { server.verify(); }
    String url() { return "/product/" + id + "/availability"; }
    void address(String json) {
        server.expect(requestTo("https://viacep.com.br/ws/08773380/json/"))
            .andRespond(withSuccess(json,MediaType.APPLICATION_JSON));
    }
    @Test void sameCityReturnsTrue() throws Exception {
        address("{\"localidade\":\"Mogi das Cruzes\",\"uf\":\"SP\"}");
        mvc.perform(get(url()).param("cep","08773380")).andExpect(status().isOk()).andExpect(content().string("true"));
    }
    @Test void differentCityReturnsFalse() throws Exception {
        address("{\"localidade\":\"Recife\"}");
        mvc.perform(get(url()).param("cep","08773380")).andExpect(status().isOk()).andExpect(content().string("false"));
    }
    @Test void hyphenAndCityNormalization() throws Exception {
        address("{\"localidade\":\"  MÓGI  DAS CRUZES  \"}");
        mvc.perform(get(url()).param("cep","08773-380")).andExpect(status().isOk()).andExpect(content().string("true"));
    }
    @ParameterizedTest @ValueSource(strings={"123","abcdefgh","08773--380","087733800"," 08773380",""})
    void rejectsMalformedCepWithoutCallingProvider(String cep) throws Exception {
        mvc.perform(get(url()).param("cep",cep)).andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400));
    }
    @Test void missingCep() throws Exception {
        mvc.perform(get(url())).andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400));
    }
    @Test void nonexistentProductDoesNotCallProvider() throws Exception {
        mvc.perform(get("/product/inexistente/availability").param("cep","08773380"))
            .andExpect(status().isNotFound()).andExpect(jsonPath("$.status").value(404));
    }
    @ParameterizedTest @ValueSource(strings={"true","\"true\""})
    void nonexistentCep(String flag) throws Exception {
        address("{\"erro\":"+flag+"}");
        mvc.perform(get(url()).param("cep","08773380")).andExpect(status().isNotFound()).andExpect(jsonPath("$.message").value("CEP não encontrado"));
    }
    @Test void providerError() throws Exception {
        server.expect(requestTo("https://viacep.com.br/ws/08773380/json/")).andRespond(withServerError());
        mvc.perform(get(url()).param("cep","08773380")).andExpect(status().isServiceUnavailable());
    }
    @Test void connectionError() throws Exception {
        server.expect(requestTo("https://viacep.com.br/ws/08773380/json/")).andRespond(withException(new ConnectException("offline")));
        mvc.perform(get(url()).param("cep","08773380")).andExpect(status().isServiceUnavailable());
    }
    @Test void timeout() throws Exception {
        server.expect(requestTo("https://viacep.com.br/ws/08773380/json/")).andRespond(withException(new SocketTimeoutException("timeout")));
        mvc.perform(get(url()).param("cep","08773380")).andExpect(status().isGatewayTimeout());
    }
    @ParameterizedTest @ValueSource(strings={"{}","null","{\"localidade\":\"\"}","not-json"})
    void invalidProviderResponse(String json) throws Exception {
        address(json);
        mvc.perform(get(url()).param("cep","08773380")).andExpect(status().isBadGateway());
    }
    @Test void crudLifecycle() throws Exception {
        var data = new RequestProduct(null,"Novo",500,"categoria","Recife");
        String body = mvc.perform(post("/product").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(data)))
            .andExpect(status().isCreated()).andExpect(header().exists("Location"))
            .andExpect(jsonPath("$.active").value(true)).andReturn().getResponse().getContentAsString();
        String createdId = mapper.readTree(body).get("id").asText();
        mvc.perform(get("/product/"+createdId)).andExpect(status().isOk()).andExpect(jsonPath("$.distributionCenter").value("Recife"));
        var changed = new RequestProduct(createdId,"Editado",900,"outra","Porto Alegre");
        mvc.perform(put("/product").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(changed)))
            .andExpect(status().isOk()).andExpect(jsonPath("$.name").value("Editado"))
            .andExpect(jsonPath("$.price").value(900)).andExpect(jsonPath("$.category").value("outra"))
            .andExpect(jsonPath("$.distributionCenter").value("Porto Alegre"));
        mvc.perform(delete("/product/"+createdId)).andExpect(status().isNoContent());
        mvc.perform(get("/product/"+createdId)).andExpect(status().isNotFound());
        mvc.perform(get("/product")).andExpect(jsonPath("$[*].id",not(hasItem(createdId))));
        mvc.perform(get("/product/"+createdId+"/availability").param("cep","08773380")).andExpect(status().isNotFound());
        assertThat(repository.findById(createdId).orElseThrow().getActive()).isFalse();
    }
    @Test void filtersAndAllFourInputTypes() throws Exception {
        mvc.perform(get("/product").param("category","TESTE")).andExpect(status().isOk()).andExpect(jsonPath("$[0].id").value(id));
        mvc.perform(get("/product/category/teste").param("categoryAsParam","teste")
            .header("categoryAsHeader","teste").contentType(MediaType.APPLICATION_JSON).content("{\"category\":\"teste\"}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$[0].id").value(id));
        mvc.perform(get("/product/category/teste").header("categoryAsHeader","outra")).andExpect(status().isBadRequest());
    }
    @Test void top5IsDescendingAndExcludesInactive() throws Exception {
        for(int i=0;i<6;i++) repository.save(new Product(new RequestProduct(null,"Top",100000+i,"top","Recife")));
        Product inactive = new Product(new RequestProduct(null,"Inativo",200000,"top","Recife"));
        inactive.setActive(false); repository.save(inactive);
        mvc.perform(get("/product/top5")).andExpect(status().isOk()).andExpect(jsonPath("$",hasSize(5)))
            .andExpect(jsonPath("$[0].price").value(100005)).andExpect(jsonPath("$[4].price").value(100001));
    }
    @Test void inputValidation() throws Exception {
        mvc.perform(post("/product").contentType(MediaType.APPLICATION_JSON)
            .content("{\"name\":\"\",\"price\":-1,\"category\":\"\",\"distributionCenter\":\"São Paulo\"}"))
            .andExpect(status().isBadRequest()).andExpect(jsonPath("$.message",containsString("distributionCenter")));
        mvc.perform(post("/product").contentType(MediaType.APPLICATION_JSON).content("{}"))
            .andExpect(status().isBadRequest());
        mvc.perform(post("/product").contentType(MediaType.APPLICATION_JSON).content("broken"))
            .andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400));
        mvc.perform(put("/product").contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(new RequestProduct(null,"Produto",100,"teste","Recife"))))
            .andExpect(status().isBadRequest());
    }
    @Test void missingProductsUseHttp404() throws Exception {
        mvc.perform(get("/product/nao-existe")).andExpect(status().isNotFound());
        mvc.perform(delete("/product/nao-existe")).andExpect(status().isNotFound());
        mvc.perform(put("/product").contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(new RequestProduct("nao-existe","Produto",100,"teste","Recife"))))
            .andExpect(status().isNotFound());
    }
    @Test void flywaySeedsHaveDistributionCenters() {
        var seeded = repository.findAll().stream().filter(p -> !p.getId().equals(id)).toList();
        assertThat(seeded).hasSize(20);
        assertThat(seeded).allSatisfy(p -> assertThat(p.getDistributionCenter()).isIn("Mogi das Cruzes","Recife","Porto Alegre"));
        assertThat(seeded.stream().map(Product::getDistributionCenter).distinct().count()).isEqualTo(3);
    }
}
