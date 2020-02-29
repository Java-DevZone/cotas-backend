package com.javadevzone.cotas.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javadevzone.cotas.CotasApplication;
import com.javadevzone.cotas.entity.Wallet;
import com.javadevzone.cotas.repository.WalletRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.ResultMatcher.matchAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = CotasApplication.class,  webEnvironment = WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
public class WalletControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WalletRepository walletRepository;

    @LocalServerPort
    private int porta;

    @Test
    public void should_create_a_wallet_and_return_with_id() throws Exception {
        Wallet alaska = Wallet.builder().name("Alaska Black").build();

        MvcResult mvcResult = mockMvc.perform(post("/wallet")
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .content(mapper.writeValueAsString(alaska)))
                .andExpect(matchAll(status().isCreated()))
                .andReturn();

        Wallet walletResult = mapper.readValue(mvcResult.getResponse().getContentAsString(), Wallet.class);

        assertThat(BigDecimal.ONE)
                .isEqualTo(walletResult.getQuota());
    }

    @Test
    public void should_get_a_wallet_and_return() throws Exception {
        Wallet savedAlaska = walletRepository.save(
                Wallet.builder()
                        .createdAt(LocalDateTime.now())
                        .quota(BigDecimal.ONE)
                        .name("Alaska Black")
                        .build());

        System.out.println("======================================================");
        System.out.println(porta);

        MvcResult mvcResult = mockMvc.perform(get("/wallet/" + savedAlaska.getId())
                .contentType(APPLICATION_JSON))
                .andExpect(matchAll(status().isOk()))
                .andReturn();

        Wallet dbWallet = walletRepository.findById(savedAlaska.getId())
                .orElseThrow(() -> new RuntimeException("Unable to find wallet"));

        Wallet walletResult = mapper.readValue(mvcResult.getResponse().getContentAsString(), Wallet.class);

        assertThat(dbWallet.getName())
                .isEqualTo(walletResult.getName());
    }

    @Test()
    public void should_get_a_wallet_that_doesnt_exist() throws Exception {
        long id = new Random().nextLong();
        mockMvc.perform(get("/wallet/" + id)
                .contentType(APPLICATION_JSON))
                .andExpect(matchAll(status().isNotFound()))
                .andExpect(status().reason(containsString(format("Wallet com ID [%s] não foi encontrada.", id))))
                .andReturn();
    }

    @Test
    public void should_update_a_wallet_and_return_the_new_wallet() throws Exception {
        Wallet savedAlaska = walletRepository.save(
                Wallet.builder()
                    .createdAt(LocalDateTime.now())
                    .quota(BigDecimal.ONE)
                    .name("Alaska Black")
                    .build());
        savedAlaska.setName("Alaska Black II");

        MvcResult mvcResult = mockMvc.perform(put("/wallet")
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(savedAlaska)))
                .andExpect(matchAll(status().isOk()))
                .andReturn();

        Wallet dbWallet = walletRepository.findById(savedAlaska.getId())
                .orElseThrow(() -> new RuntimeException("Unable to find wallet"));

        Wallet walletResult = mapper.readValue(mvcResult.getResponse().getContentAsString(), Wallet.class);

        assertThat(walletResult.getUpdatedAt())
                .isNotNull();
        assertThat(dbWallet.getName())
                .isEqualTo(walletResult.getName());
    }

    @Test
    public void should_delete_a_wallet_by_id_and_return_void() throws Exception {
        Wallet savedAlaska = walletRepository.save(
                Wallet.builder()
                        .createdAt(LocalDateTime.now())
                        .quota(BigDecimal.ONE)
                        .name("Alaska Black")
                        .build());

        mockMvc.perform(delete("/wallet/" + savedAlaska.getId()))
                .andExpect(matchAll(status().isNoContent()))
                .andReturn();

        assertThat(walletRepository.existsById(savedAlaska.getId()))
                .isFalse();
    }

    @Test()
    public void should_delete_a_wallet_that_doesnt_exist() throws Exception {
        long id = new Random().nextLong();
        mockMvc.perform(delete("/wallet/" + id)
                .contentType(APPLICATION_JSON))
                .andExpect(matchAll(status().isNotFound()))
                .andExpect(status().reason(containsString(format("Wallet com ID [%s] não foi encontrada.", id))))
                .andReturn();
    }

}