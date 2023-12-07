package com.westee.cake.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.westee.cake.CakeApplication;
import com.westee.cake.entity.Response;
import com.westee.cake.generate.Address;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = CakeApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {"spring.config.location=classpath:test-application.properties"})
public class AddressIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void createAddressFailedIfNotLoginTest() {
        Assertions.assertThrows(HttpClientErrorException.BadRequest.class, () ->
            doHttpRequest("/api/v1/address", HttpMethod.POST, null, objectMapper.writeValueAsString(generateAddress())));
    }

    @Test
    public void addAddressSuccessTest() throws JsonProcessingException {
        Address address = generateAddress();
        // 添加地址
        String createAddressBody = doHttpRequest("/api/v1/address", HttpMethod.POST, generateRequestHeaders(), objectMapper.writeValueAsString(address)).getBody();
        TypeReference<Response<Address>> typeReference = new TypeReference<>() {};
        Response<Address> response = objectMapper.readValue(createAddressBody, typeReference);
        Address addressResponse = response.getData();
        assertEquals(address.getAddress(), addressResponse.getAddress());

        // 获取已添加的地址
        String body = doHttpRequest("/api/v1/address/" + addressResponse.getId(), HttpMethod.GET, generateRequestHeaders(), null).getBody();
        Response<Address> addressResponse2 = objectMapper.readValue(body, typeReference);
        assertEquals(address.getAddress(), addressResponse2.getData().getAddress());
    }

    @Test
    public void updateAddressTest() throws JsonProcessingException {
        assertThrows(HttpClientErrorException.BadRequest.class, () ->
                doHttpRequest("/api/v1/address/1", HttpMethod.PATCH, null, objectMapper.writeValueAsString(generateAddress())));

        // 添加地址
        String createAddressBody = doHttpRequest("/api/v1/address", HttpMethod.POST, generateRequestHeaders(),
                objectMapper.writeValueAsString(generateAddress())).getBody();
        Response<Address> addressResponse = objectMapper.readValue(createAddressBody, new TypeReference<>() {});

        Address data = addressResponse.getData();
        data.setName("modified address");

        // 更新
        String body = doHttpRequest("/api/v1/address/1", HttpMethod.PATCH, generateRequestHeaders(),
                objectMapper.writeValueAsString(data)).getBody();
        Response<Address> newAddressResponse = objectMapper.readValue(body, new TypeReference<>() {});
        assertEquals(data.getName() ,newAddressResponse.getData().getName());
    }

    @Test
    public void deleteAddressTest() throws JsonProcessingException {
        // 添加地址
        String createAddressBody = doHttpRequest("/api/v1/address", HttpMethod.POST, generateRequestHeaders(),
                objectMapper.writeValueAsString(generateAddress())).getBody();

        Response<Address> addressResponse = objectMapper.readValue(createAddressBody, new TypeReference<>() {});

        // 不传cookie进行删除报错400
        assertThrows(HttpClientErrorException.BadRequest.class, () ->
            doHttpRequest("/api/v1/address/" + addressResponse.getData().getId(), HttpMethod.DELETE, null, null));

        // 传递cookie进行删除成功
        doHttpRequest("/api/v1/address/" + addressResponse.getData().getId(), HttpMethod.DELETE, generateRequestHeaders(), null);

        // 获取地址失败
        assertThrows(HttpClientErrorException.NotFound.class, () ->
            doHttpRequest("/api/v1/address/" + addressResponse.getData().getId(), HttpMethod.GET, generateRequestHeaders(), null));
    }

    public Address generateAddress() {
        Address address = new Address();
        address.setUserId(1L);
        address.setAddress("test");
        address.setDefaultAddress(true);
        address.setSpecificAddress("test specific");
        address.setName("test specific");
        address.setPhoneNumber("15611111111");
        address.setLongitude(BigDecimal.valueOf(1));
        address.setLatitude(BigDecimal.TEN);
        return address;
    }

    public MultiValueMap<String, String> generateRequestHeaders()  {
        UserLoginResponse userLoginResponse;
        try {
            userLoginResponse = loginAndGetToken();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        String cookie = userLoginResponse.getCookie();
        String token = userLoginResponse.getToken();
        // 根据cookie生成headers
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("cookie", cookie);
        headers.add("Token", token);

        return headers;
    }
}
