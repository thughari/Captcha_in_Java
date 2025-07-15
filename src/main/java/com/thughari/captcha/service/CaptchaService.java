package com.thughari.captcha.service;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class CaptchaService {
	
	private final Map<String, String> captchaMapWithIP = new ConcurrentHashMap<String, String>();
	
	private static final int WIDTH = 150;
    private static final int HEIGHT = 50;
    private static final String CHARACTERS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    
    public String generateCaptcha(HttpServletRequest httpServletRequest) {
        String captchaText = generateRandomText(6);
        String clientIP = getClientIp(httpServletRequest);
        captchaMapWithIP.put(clientIP, captchaText);
        System.out.println("Catpcha:::::::::: "+captchaText);
        
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        
        // Background color
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, WIDTH, HEIGHT);
        
        // Font settings
        g.setFont(new Font("Arial", Font.BOLD, 28));
        g.setColor(Color.BLACK);
        
        // Draw text with random noise
        Random rand = new Random();
        for (int i = 0; i < captchaText.length(); i++) {
            g.drawString(String.valueOf(captchaText.charAt(i)), 20 + (i * 20), 35 + (rand.nextInt(10) - 5));
        }
        
        // Add some noise
        for (int i = 0; i < 5; i++) {
            g.setColor(new Color(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255)));
            g.drawLine(rand.nextInt(WIDTH), rand.nextInt(HEIGHT), rand.nextInt(WIDTH), rand.nextInt(HEIGHT));
        }
        
        g.dispose();
        
        // Convert image to Base64 string
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", baos);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "data:image/png;base64," + Base64.getEncoder().encodeToString(baos.toByteArray());
    }
    
    public boolean validateCaptcha(HttpServletRequest httpServletRequest) {
    	String clientIp = getClientIp(httpServletRequest);
    	String incomingCaptcha = (String) httpServletRequest.getParameter("captcha");
    	return captchaMapWithIP.get(clientIp).equals(incomingCaptcha);
    }
    
    private String generateRandomText(int length) {
        SecureRandom rand = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(CHARACTERS.charAt(rand.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }
    
    private String getClientIp(HttpServletRequest request) {
		String forwardedFor = request.getHeader("X-Forwarded-For");
		if (forwardedFor != null && !forwardedFor.isEmpty()) {
			return forwardedFor.split(",")[0].trim();
		}
		String realIp = request.getHeader("X-Real-IP");
		if (realIp != null && !realIp.isEmpty()) {
			return realIp;
		}
		return request.getRemoteAddr();
	}

}
