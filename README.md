# Captcha in Java

A simple Spring Boot application that generates and validates CAPTCHAs using Java’s `Graphics2D` API.  
It helps prevent automated bot submissions by creating random text images with noise and distortion.

## Features
- Generate 6-character random CAPTCHAs  
- Add noise and distortion for security  
- Validate CAPTCHA input per client IP  
- Testable with a front-end page (`index.html`)

## Requirements
- Java 17  
- Maven

## Run the Project
```bash
git clone https://github.com/thughari/Captcha_in_Java.git
cd Captcha_in_Java
mvn spring-boot:run
```
Then open:
👉 http://localhost:8080/index.html
 to test the CAPTCHA.
