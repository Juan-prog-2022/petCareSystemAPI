
package com.bluesoftware.petshop.services;

import com.bluesoftware.petshop.entities.*;
import com.bluesoftware.petshop.exceptions.*;
import com.bluesoftware.petshop.repositories.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class OrderServiceImpl implements IOrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    public OrderServiceImpl(OrderRepository orderRepository,
                            CustomerRepository customerRepository,
                            ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    @Override
    public Order createOrder(Integer customerId, List<OrderItem> items) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("customer.not.found"));

        Order order = new Order();
        order.setCustomer(customer);
        order.setStatus(OrderStatus.PENDING);

        List<OrderItem> finalItems = new ArrayList<>();
        double total = 0;

        for (OrderItem item : items) {

            // 🔹 Buscar producto real
            Product product = productRepository.findById(item.getProduct().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("product.not.found"));

            // 🔥 VALIDAR STOCK
            if (product.getStock() < item.getQuantity()) {
                throw new BadRequestException("product.stock.insufficient");
            }

            // 🔥 DESCONTAR STOCK
            product.setStock(product.getStock() - item.getQuantity());

            double price = product.getPrice();
            double subtotal = price * item.getQuantity();

            // 🔹 Armar item final
            OrderItem newItem = new OrderItem();
            newItem.setOrder(order);
            newItem.setProduct(product);
            newItem.setQuantity(item.getQuantity());
            newItem.setPrice(price);
            newItem.setSubtotal(subtotal);

            finalItems.add(newItem);
            total += subtotal;
        }

        order.setItems(finalItems);
        order.setTotal(total);
        order.setPaymentStatus(PaymentStatus.PENDING);

        return orderRepository.save(order);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .filter(Order::isActive)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public Order getOrderById(Integer id) {
        return orderRepository.findById(id)
                .filter(Order::isActive)
                .orElseThrow(() -> new ResourceNotFoundException("order.not.found"));
    }

    @Transactional
    @Override
    public void cancelOrder(Integer id) {

        Order order = getOrderById(id);

        // 🔥 DEVOLVER STOCK
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantity());
        }

        order.setStatus(OrderStatus.CANCELLED);
        order.setActive(false);

        orderRepository.save(order);
    }
}