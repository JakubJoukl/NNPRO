package com.example.nnprorocnikovyprojekt.external;

import com.example.nnprorocnikovyprojekt.advice.exceptions.UnauthorizedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class CaptchaService {

    @Value("${recaptcha.secret}")
    private String secretKey;

    public boolean validateCaptcha(String captchaResponse) {
        String url = "https://www.google.com/recaptcha/api/siteverify";

        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.postForObject(
                UriComponentsBuilder.fromHttpUrl(url)
                        .queryParam("secret", secretKey)
                        .queryParam("response", captchaResponse)
                        .toUriString(),
                null,
                String.class
        );

        if(response == null) throw new UnauthorizedException("Failed to get response or the response is invalid");

        return response.contains("\"success\": true");
    }
}