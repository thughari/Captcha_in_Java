package com.thughari.captcha.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.thughari.captcha.service.CaptchaService;

@WebMvcTest(CaptchaController.class)
class CaptchaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CaptchaService captchaService;

    @Test
    void generateCaptchaReturnsImagePayload() throws Exception {
        when(captchaService.generateCaptcha(any())).thenReturn("data:image/png;base64,test");

        mockMvc.perform(get("/api/captcha/generate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.image").value("data:image/png;base64,test"));
    }

    @Test
    void validateCaptchaReturnsSuccessWhenServiceReturnsTrue() throws Exception {
        when(captchaService.validateCaptcha(any())).thenReturn(true);

        mockMvc.perform(post("/api/captcha/validate").param("captcha", "ABC123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void validateCaptchaReturnsFailWhenServiceReturnsFalse() throws Exception {
        when(captchaService.validateCaptcha(any())).thenReturn(false);

        mockMvc.perform(post("/api/captcha/validate").param("captcha", "BAD000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("fail"));
    }
}
