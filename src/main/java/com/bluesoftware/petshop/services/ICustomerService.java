package com.bluesoftware.petshop.services;

import java.util.List;

import com.bluesoftware.petshop.entities.Customer;

public interface ICustomerService {

    List<Customer> getAllCustomers();

    Customer getCustomerById(Integer id);

    Customer createCustomer(Customer customer);

    Customer updateCustomer(Integer id, Customer customer);

    void deleteCustomer(Integer id);

}
