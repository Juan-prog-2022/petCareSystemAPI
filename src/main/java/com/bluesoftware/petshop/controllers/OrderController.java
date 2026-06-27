package com.bluesoftware.petshop.controllers;

import com.bluesoftware.petshop.dtos.*;
import com.bluesoftware.petshop.entities.*;
import com.bluesoftware.petshop.repositories.CustomerRepository;
import com.bluesoftware.petshop.repositories.OrderRepository;
import com.bluesoftware.petshop.services.IOrderService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final IOrderService orderService;
    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;

    public OrderController(IOrderService orderService,
                           CustomerRepository customerRepository,
                           OrderRepository orderRepository) {
        this.orderService = orderService;
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
    }

    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(
            @Valid @RequestBody OrderRequestDTO request) {

        List<OrderItem> items = mapToItems(request.getItems());

        Order order = orderService.createOrder(request.getCustomerId(), items);

        return ResponseEntity.ok(mapToResponse(order));
    }

    @GetMapping("/my-orders")
    public ResponseEntity<List<OrderResponseDTO>> getMyOrders(Authentication authentication) {

        String email = authentication.getName();

        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        List<Order> orders = orderRepository.findByCustomerId(customer.getId());

        List<OrderResponseDTO> response = orders.stream()
                .filter(Order::isActive)
                .map(this::mapToResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/my-orders")
    public ResponseEntity<OrderResponseDTO> createMyOrder(
            @Valid @RequestBody CreateMyOrderRequest request,
            Authentication authentication) {

        String email = authentication.getName();

        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        List<OrderItem> items = mapToItems(request.getItems());

        Order order = orderService.createOrder(customer.getId(), items);

        return ResponseEntity.ok(mapToResponse(order));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> getAllOrders() {

        List<OrderResponseDTO> response = orderService.getAllOrders()
                .stream()
                .map(this::mapToResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable Integer id) {

        Order order = orderService.getOrderById(id);

        return ResponseEntity.ok(mapToResponse(order));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIAN')")
    @PutMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelOrder(@PathVariable Integer id) {

        orderService.cancelOrder(id);

        return ResponseEntity.noContent().build();
    }

    private List<OrderItem> mapToItems(List<OrderItemDTO> dtos) {
        return dtos.stream().map(dto -> {
            OrderItem item = new OrderItem();

            Product product = new Product();
            product.setId(dto.getProductId());

            item.setProduct(product);
            item.setQuantity(dto.getQuantity());

            return item;
        }).toList();
    }

    private OrderResponseDTO mapToResponse(Order order) {

        OrderResponseDTO dto = new OrderResponseDTO();

        dto.setId(order.getId());
        dto.setCustomerId(order.getCustomer().getId());
        dto.setCustomerName(order.getCustomer().getName());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setTotal(order.getTotal());
        dto.setStatus(order.getStatus());
        dto.setPaymentStatus(order.getPaymentStatus() != null ? order.getPaymentStatus().name() : null);
        dto.setMpPreferenceId(order.getMpPreferenceId());
        dto.setMpPaymentId(order.getMpPaymentId());

        List<OrderItemResponseDTO> items = order.getItems().stream().map(item -> {
            OrderItemResponseDTO itemDTO = new OrderItemResponseDTO();

            itemDTO.setProductId(item.getProduct().getId());
            itemDTO.setProductName(item.getProduct().getName());
            itemDTO.setQuantity(item.getQuantity());
            itemDTO.setPrice(item.getPrice());
            itemDTO.setSubtotal(item.getSubtotal());

            return itemDTO;
        }).toList();

        dto.setItems(items);

        return dto;
    }
}
