package com.bluesoftware.petshop.services;

import com.bluesoftware.petshop.entities.Order;
import com.bluesoftware.petshop.entities.OrderItem;

import java.util.List;

public interface IOrderService {

    Order createOrder(Integer customerId, List<OrderItem> items);

    List<Order> getAllOrders();

    Order getOrderById(Integer id);

    void cancelOrder(Integer id);
}
