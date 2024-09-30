package fa.training.backend.controller;

import fa.training.backend.dto.request.customer.CustomerAddRequest;
import fa.training.backend.dto.request.customer.CustomerUpdateRequest;
import fa.training.backend.dto.response.customer.CustomerDto;
import fa.training.backend.dto.response.PageDto;
import fa.training.backend.dto.response.Response;
import fa.training.backend.service.CustomerService;
import fa.training.backend.util.Message;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {
    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public ResponseEntity<Response<PageDto<CustomerDto>>> getAllCustomers(
            @RequestParam(defaultValue = "1", required = false) int page,
            @RequestParam(defaultValue = "10", required = false) int size,
            @RequestParam(defaultValue = "", required = false) String search
    ) {
        Pageable pageable = PageRequest.of(page - 1, size);
        PageDto<CustomerDto> customers = customerService.getAll(pageable, search);
        return ResponseEntity.ok(new Response<>(Message.MSG_211.getCode(), Message.MSG_211.getMessage(), customers));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> getCustomerById(@PathVariable String id) {
        CustomerDto customer = customerService.getById(id);
        return ResponseEntity.ok(Response.builder()
                        .code(Message.MSG_213.getCode())
                        .description(Message.MSG_213.getMessage())
                        .data(customer)
                        .build());
    }

    @PostMapping
    public ResponseEntity<Response> addCustomer(@Valid @RequestBody CustomerAddRequest customerAddRequest) {
        boolean result = customerService.add(customerAddRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Response.builder()
                        .code(Message.MSG_212.getCode())
                        .description(Message.MSG_212.getMessage())
                        .data(result)
                        .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Response> updateCustomer(@Valid @RequestBody CustomerUpdateRequest customerUpdateRequest, @PathVariable String id) {
        boolean updated = customerService.update(customerUpdateRequest, id);
        return ResponseEntity.ok(Response.builder()
                        .code(Message.MSG_214.getCode())
                        .description(Message.MSG_214.getMessage())
                        .data(updated)
                        .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response> deleteCustomer(@PathVariable String id) {
        customerService.softDelete(id);
        return ResponseEntity.ok(Response.builder()
                        .code(Message.MSG_216.getCode())
                        .description(Message.MSG_216.getMessage())
                        .build());
    }

    @GetMapping("/names")
    public ResponseEntity<Response> getCustomerNames() {
        return ResponseEntity.ok(new Response<>(
                Message.MSG_213.getCode(),
                Message.MSG_213.getMessage(),
                customerService.getCustomerName()
        ));
    }
}
