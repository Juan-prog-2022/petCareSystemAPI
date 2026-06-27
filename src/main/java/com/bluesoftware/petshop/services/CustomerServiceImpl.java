package com.bluesoftware.petshop.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bluesoftware.petshop.entities.Customer;
import com.bluesoftware.petshop.exceptions.BadRequestException;
import com.bluesoftware.petshop.exceptions.ResourceNotFoundException;
import com.bluesoftware.petshop.repositories.CustomerRepository;

@Service
public class CustomerServiceImpl implements ICustomerService {

    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll()
                .stream()
                .filter(Customer::isActive)
                .toList();
    }

    @Transactional
    @Override
    public Customer createCustomer(Customer customer) {

        if (customerRepository.existsByEmail(customer.getEmail())) {
            throw new BadRequestException("customer.email.exists");
        }

        return customerRepository.save(customer);
    }

    @Transactional(readOnly = true)
    @Override
    public Customer getCustomerById(Integer id) {
        return customerRepository.findById(id)
                .filter(Customer::isActive)
                .orElseThrow(() -> new ResourceNotFoundException("customer.not.found"));
    }

    @Transactional
    @Override
    public Customer updateCustomer(Integer id, Customer customerData) {

        Customer customer = getCustomerById(id);

        // Validar email si cambia
        if (!customer.getEmail().equals(customerData.getEmail()) &&
                customerRepository.existsByEmail(customerData.getEmail())) {
            throw new BadRequestException("customer.email.exists");
        }

        customer.setName(customerData.getName());
        customer.setPhone(customerData.getPhone());
        customer.setAddress(customerData.getAddress());
        customer.setEmail(customerData.getEmail());

        return customerRepository.save(customer);
    }

    @Transactional
    @Override
    public void deleteCustomer(Integer id) {
        Customer customer = getCustomerById(id);
        customer.setActive(false); // soft delete
        customerRepository.save(customer);
    }
}