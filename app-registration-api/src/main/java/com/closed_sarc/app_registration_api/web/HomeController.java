package com.closed_sarc.app_registration_api.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/")
    public Map<String, Object> home() {
        Map<String, Object> info = new HashMap<>();
        info.put("application", "Registration API");
        info.put("version", "1.0.0");
        info.put("status", "UP");
        
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("swagger", "/swagger-ui.html");
        endpoints.put("api-docs", "/v3/api-docs");
        endpoints.put("health", "/actuator/health");
        endpoints.put("metrics", "/actuator/metrics");
        endpoints.put("prometheus", "/actuator/prometheus");
        
        info.put("endpoints", endpoints);
        
        return info;
    }
}
