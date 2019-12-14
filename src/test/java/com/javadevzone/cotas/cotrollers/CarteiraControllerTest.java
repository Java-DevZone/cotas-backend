package com.javadevzone.cotas.cotrollers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javadevzone.cotas.entity.Carteira;
import com.javadevzone.cotas.services.CarteiraService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.lang.annotation.ElementType;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.ResultMatcher.matchAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CarteiraControllerTest {

    @Autowired
    private CarteiraController target;

    @Autowired
    private CarteiraService service;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void should_create_a_carteira_and_return() throws Exception {
        Carteira alaska = Carteira.builder().nome("Alaska Black").build();

        String alaskaJson = mapper.writeValueAsString(alaska);

        mockMvc.perform(post("/carteira")
            .contentType(APPLICATION_JSON)
            .content(alaskaJson)
        ).andExpect(matchAll(status().isCreated()));
    }

}