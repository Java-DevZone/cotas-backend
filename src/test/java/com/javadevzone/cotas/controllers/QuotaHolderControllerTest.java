package com.javadevzone.cotas.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javadevzone.cotas.CotasApplication;
import com.javadevzone.cotas.entity.QuotaHolder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.ResultMatcher.matchAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = CotasApplication.class,  webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
public class QuotaHolderControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void should_create_a_new_quota_holder() throws Exception {
        QuotaHolder mrs_lovely = QuotaHolder.builder().name("Mrs Lovely").build();

        MvcResult mvcResult = mockMvc.perform(post("/quotaHolder")
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .content(mapper.writeValueAsString(mrs_lovely)))
                .andExpect(matchAll(status().isCreated()))
                .andReturn();

        QuotaHolder investidor = mapper.readValue(mvcResult.getResponse().getContentAsString(), QuotaHolder.class);

        assertThat(investidor.getId())
                .isNotNull();

    }

    @Test
    public void should_throw_exception_when_creating_new_quota_holder_without_name() throws Exception {
        mockMvc.perform(post("/quotaHolder")
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .content(mapper.writeValueAsString(new QuotaHolder())))
                .andExpect(matchAll(status().is4xxClientError()))
                .andReturn();

    }

}
