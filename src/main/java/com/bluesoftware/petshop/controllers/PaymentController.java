package com.bluesoftware.petshop.controllers;

import com.bluesoftware.petshop.dtos.CreatePreferenceResponse;
import com.bluesoftware.petshop.entities.Order;
import com.bluesoftware.petshop.entities.PaymentStatus;
import com.bluesoftware.petshop.repositories.OrderRepository;
import com.bluesoftware.petshop.services.MercadoPagoService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final MercadoPagoService mercadoPagoService;
    private final OrderRepository orderRepository;

    public PaymentController(MercadoPagoService mercadoPagoService,
                             OrderRepository orderRepository) {
        this.mercadoPagoService = mercadoPagoService;
        this.orderRepository = orderRepository;
    }

    @PostMapping("/create-preference/{orderId}")
    public ResponseEntity<CreatePreferenceResponse> createPreference(
            @PathVariable Integer orderId,
            Authentication authentication) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));

        String email = authentication.getName();
        if (!order.getCustomer().getEmail().equals(email)) {
            return ResponseEntity.status(403).build();
        }

        String preferenceId = mercadoPagoService.createPreference(order);
        String checkoutUrl = mercadoPagoService.getCheckoutUrl(preferenceId);

        order.setMpPreferenceId(preferenceId);
        order.setPaymentStatus(PaymentStatus.PENDING);
        orderRepository.save(order);

        CreatePreferenceResponse response = new CreatePreferenceResponse();
        response.setPreferenceId(preferenceId);
        response.setCheckoutUrl(checkoutUrl);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(@RequestBody Map<String, Object> payload) {
        String topic = (String) payload.get("topic");
        if ("payment".equals(topic) || "merchant_order".equals(topic)) {
            String resourceId = payload.get("id").toString();
            System.out.println("Webhook recibido: " + topic + " ID: " + resourceId);
        }
        return ResponseEntity.ok("OK");
    }
}
