package com.chirag.doctorappointmentsystem.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class SmsService {

    // Replace with your actual API key and device ID from textbee.dev
    private final String API_KEY = "bb32303d-51e8-4fb9-b1cb-e683b0775ff5";
    private final String DEVICE_ID = "6809b70360ed7610f70fa615";
    private final String BASE_URL = "https://api.textbee.dev/api/v1";

    private final RestTemplate restTemplate = new RestTemplate();

    public boolean sendSms(String to, String message) {
        String url = BASE_URL + "/gateway/devices/" + DEVICE_ID + "/send-sms";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-api-key", API_KEY);

        Map<String, Object> body = new HashMap<>();
        body.put("recipients", Collections.singletonList(to));
        body.put("message", message);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
