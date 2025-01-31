package com.thughari.captcha.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thughari.captcha.service.CaptchaService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;


@RestController
@RequestMapping("/api/captcha")
@CrossOrigin(origins = "*")
public class CaptchaController {
	
	@Autowired
	private CaptchaService captchaService;
	
	@GetMapping("/generate")
    public ResponseEntity<Map<String, String>> generateCaptcha(HttpServletRequest httpServletRequest) {
        Map<String, String> response = new HashMap<>();
        String captchaImage = captchaService.generateCaptcha(httpServletRequest);
        response.put("image", captchaImage);
        return ResponseEntity.ok(response);
    }
	
	@PostMapping("/validate")
    public ResponseEntity<Map<String, String>> validateCaptcha(HttpServletRequest httpServletRequest) {
        String storedCaptcha = (String) httpServletRequest.getHeader("captcha");
        System.out.println(storedCaptcha);
        boolean isValid = captchaService.validateCaptcha(httpServletRequest);
        Map<String, String> response = new HashMap<>();
        response.put("status", isValid ? "success" : "fail");
        return ResponseEntity.ok(response);
    }
	

}
