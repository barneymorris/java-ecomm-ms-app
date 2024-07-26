package com.barneymorris.ecommerce.customer;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<String> createCustomer(
            @RequestBody @Valid CustomerRequest request
    ) {
        return ResponseEntity.ok(customerService.createCustomer(request));
    }

    @PutMapping
    public ResponseEntity<Void> updateCustomer(
            @RequestBody @Valid CustomerRequest request
    ) {
        customerService.updateCustomer(request);

        return ResponseEntity.accepted().build();
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponse>> findAll() {
        return ResponseEntity.ok(customerService.findAllCustomers());
    }

    @GetMapping("/exists/{customerId}")
    public ResponseEntity<Boolean> existsById(
            @PathVariable String customerId
    ) {
        return ResponseEntity.ok(customerService.existsById(customerId));
    }

    @GetMapping("/exists/{customer-id}")
    public ResponseEntity<CustomerResponse> findById(
            @PathVariable String customerId
    ) {
        return ResponseEntity.ok(customerService.findById(customerId));
    }

    @DeleteMapping("/customerId")
    public ResponseEntity<Void> delete(
            @PathVariable String customerId
    ) {
        customerService.deleteCustomer(customerId);

        return ResponseEntity.accepted().build();
    }
}
