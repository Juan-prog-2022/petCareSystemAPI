package com.bluesoftware.petshop.services;

import com.bluesoftware.petshop.config.MercadoPagoConfig;
import com.bluesoftware.petshop.entities.Order;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class MercadoPagoService {

    private static final String MP_API_URL = "https://api.mercadopago.com/checkout/preferences";

    private final MercadoPagoConfig config;
    private final RestTemplate restTemplate;

    public MercadoPagoService(MercadoPagoConfig config) {
        this.config = config;
        this.restTemplate = new RestTemplate();
    }

    public String createPreference(Order order) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(config.getAccessToken());
        headers.setContentType(MediaType.APPLICATION_JSON);

        List<Map<String, Object>> items = order.getItems().stream().map(item -> Map.<String, Object>of(
                "id", item.getProduct().getId().toString(),
                "title", item.getProduct().getName(),
                "quantity", item.getQuantity(),
                "unit_price", item.getPrice(),
                "currency_id", "ARS"
        )).toList();

        Map<String, Object> body = Map.of(
                "items", items,
                "external_reference", order.getId().toString(),
                "auto_return", "approved",
                "back_urls", Map.of(
                        "success", config.getSuccessUrl(),
                        "failure", config.getFailureUrl(),
                        "pending", config.getPendingUrl()
                ),
                "notification_url", config.getSuccessUrl().replace("/orders", "/api/payments/webhook")
        );

        ResponseEntity<Map> response = restTemplate.postForEntity(
                MP_API_URL,
                new HttpEntity<>(body, headers),
                Map.class
        );

        Map<String, Object> responseBody = response.getBody();
        if (responseBody == null) {
            throw new RuntimeException("Error al crear preferencia de pago");
        }

        return responseBody.get("id").toString();
    }

    public String getCheckoutUrl(String preferenceId) {
        return "https://www.mercadopago.com.ar/checkout/v1/redirect?pref_id=" + preferenceId;
    }
}
