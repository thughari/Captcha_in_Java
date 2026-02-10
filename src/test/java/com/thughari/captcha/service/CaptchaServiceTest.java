package com.thughari.captcha.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

class CaptchaServiceTest {

    private CaptchaService captchaService;

    @BeforeEach
    void setUp() {
        captchaService = new CaptchaService();
    }

    @Test
    void generateCaptchaReturnsBase64PngDataUri() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("10.0.0.5");

        String captchaImageData = captchaService.generateCaptcha(request);

        assertTrue(captchaImageData.startsWith("data:image/png;base64,"));
        assertTrue(captchaImageData.length() > "data:image/png;base64,".length());
    }

    @Test
    void validateCaptchaReturnsTrueForSameIpAndSameCaptcha() {
        MockHttpServletRequest generateRequest = new MockHttpServletRequest();
        generateRequest.setRemoteAddr("192.168.1.10");

        captchaService.generateCaptcha(generateRequest);
        String generatedCaptcha = getStoredCaptchaForIp("192.168.1.10");

        MockHttpServletRequest validateRequest = new MockHttpServletRequest();
        validateRequest.setRemoteAddr("192.168.1.10");
        validateRequest.setParameter("captcha", generatedCaptcha);

        assertTrue(captchaService.validateCaptcha(validateRequest));
    }

    @Test
    void validateCaptchaReturnsFalseForSameIpAndDifferentCaptcha() {
        MockHttpServletRequest generateRequest = new MockHttpServletRequest();
        generateRequest.setRemoteAddr("172.16.0.4");

        captchaService.generateCaptcha(generateRequest);

        MockHttpServletRequest validateRequest = new MockHttpServletRequest();
        validateRequest.setRemoteAddr("172.16.0.4");
        validateRequest.setParameter("captcha", "WRONG1");

        assertFalse(captchaService.validateCaptcha(validateRequest));
    }

    @Test
    void xForwardedForIsUsedAsClientIdentity() {
        MockHttpServletRequest generateRequest = new MockHttpServletRequest();
        generateRequest.addHeader("X-Forwarded-For", "203.0.113.2, 10.1.1.1");
        generateRequest.setRemoteAddr("127.0.0.1");

        captchaService.generateCaptcha(generateRequest);
        String generatedCaptcha = getStoredCaptchaForIp("203.0.113.2");

        MockHttpServletRequest validateRequest = new MockHttpServletRequest();
        validateRequest.addHeader("X-Forwarded-For", "203.0.113.2, 10.1.1.1");
        validateRequest.setRemoteAddr("127.0.0.1");
        validateRequest.setParameter("captcha", generatedCaptcha);

        assertTrue(captchaService.validateCaptcha(validateRequest));
    }

    @Test
    void xRealIpIsUsedWhenForwardedForMissing() {
        MockHttpServletRequest generateRequest = new MockHttpServletRequest();
        generateRequest.addHeader("X-Real-IP", "198.51.100.21");
        generateRequest.setRemoteAddr("127.0.0.1");

        captchaService.generateCaptcha(generateRequest);
        String generatedCaptcha = getStoredCaptchaForIp("198.51.100.21");

        MockHttpServletRequest validateRequest = new MockHttpServletRequest();
        validateRequest.addHeader("X-Real-IP", "198.51.100.21");
        validateRequest.setRemoteAddr("127.0.0.1");
        validateRequest.setParameter("captcha", generatedCaptcha);

        assertTrue(captchaService.validateCaptcha(validateRequest));
    }

    private String getStoredCaptchaForIp(String ip) {
        try {
            Field field = CaptchaService.class.getDeclaredField("captchaMapWithIP");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<String, String> map = (Map<String, String>) field.get(captchaService);
            return map.get(ip);
        } catch (Exception e) {
            throw new RuntimeException("Unable to extract stored captcha", e);
        }
    }
}
