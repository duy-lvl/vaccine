package fa.training.backend.service.impl;


import fa.training.backend.dto.request.customer.CustomerAddRequest;
import fa.training.backend.dto.request.customer.CustomerUpdateRequest;
import fa.training.backend.dto.response.customer.CustomerDto;
import fa.training.backend.dto.response.customer.CustomerNameDto;
import fa.training.backend.dto.response.PageDto;
import fa.training.backend.exception.common.*;
import fa.training.backend.exception.customer.IdentityCardDuplicateException;
import fa.training.backend.model.Customer;
import fa.training.backend.model.InjectionResult;
import fa.training.backend.repository.CustomerRepository;
import fa.training.backend.repository.InjectionResultRepository;
import fa.training.backend.service.CustomerService;
import fa.training.backend.util.Message;
import fa.training.backend.util.Status;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final ModelMapper modelMapper;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private InjectionResultRepository injectionResultRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository, PasswordEncoder passwordEncoder, ModelMapper modelMapper) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
    }

    public boolean add(CustomerAddRequest customerAddRequest) {
        boolean isIdentityCardExisted = customerRepository.existsByIdentityCard(customerAddRequest.getIdentityCard());
        if (isIdentityCardExisted) {
            throw new IdentityCardDuplicateException(Message.MSG_112);
        }
        boolean isEmailExisted = customerRepository.existsByEmail(customerAddRequest.getEmail());
        if (isEmailExisted) {
            throw new EmailDuplicateException(Message.MSG_115);
        }
        boolean isPhoneExisted = customerRepository.existsByPhone(customerAddRequest.getPhone());
        if (isPhoneExisted) {
            throw new PhoneDuplicateException(Message.MSG_113);
        }
        boolean isUserNameExisted = customerRepository.existsByUsername(customerAddRequest.getUsername());
        if (isUserNameExisted) {
            throw new UsernameDuplicateException(Message.MSG_114);
        }
        Customer customer = modelMapper.map(customerAddRequest, Customer.class);
        customer.setPassword(passwordEncoder.encode(customerAddRequest.getPassword()));

        customer.setStatus(Status.ACTIVE);
        customerRepository.save(customer);
        return true;
    }

    public boolean update(CustomerUpdateRequest customerUpdateRequest, String id) {
        Optional<Customer> existingCustomerOptional = customerRepository.findById(id);
        if (existingCustomerOptional.isEmpty()) {
            throw new NotFoundException(Message.MSG_111, id);
        }
        Customer existingCustomer = existingCustomerOptional.get();
        if (!existingCustomer.getIdentityCard().equals(customerUpdateRequest.getIdentityCard()) &&
                customerRepository.existsByIdentityCard(customerUpdateRequest.getIdentityCard())) {
            throw new IdentityCardDuplicateException(Message.MSG_112);
        }
        if (!existingCustomer.getEmail().equals(customerUpdateRequest.getEmail()) &&
                customerRepository.existsByEmail(customerUpdateRequest.getEmail())) {
            throw new EmailDuplicateException(Message.MSG_115);
        }
        if (!existingCustomer.getPhone().equals(customerUpdateRequest.getPhone()) &&
                customerRepository.existsByPhone(customerUpdateRequest.getPhone())) {
            throw new PhoneDuplicateException(Message.MSG_113);
        }
        if (!existingCustomer.getUsername().equals(customerUpdateRequest.getUsername()) &&
                customerRepository.existsByUsername(customerUpdateRequest.getUsername())) {
            throw new UsernameDuplicateException(Message.MSG_114);
        }
        modelMapper.map(customerUpdateRequest, existingCustomer);
        customerRepository.save(existingCustomer);
        return true;
    }

    public PageDto<CustomerDto> getAll(Pageable pageable, String search) {
        var page = customerRepository.findByFullNameContainsIgnoreCase(pageable, search);
        PageDto<CustomerDto> pageDto = new PageDto<>();
        List<CustomerDto> customerDtos = page.stream().map(customer -> modelMapper.map(customer, CustomerDto.class)).toList();
        pageDto.setData(customerDtos);
        pageDto.setTotalPages(page.getTotalPages());
        pageDto.setPage(pageable.getPageNumber() + 1);
        pageDto.setSize(pageable.getPageSize());
        pageDto.setTotalElements(page.getTotalElements());
        return pageDto;
    }

    public CustomerDto getById(String id) {
        Optional<Customer> existingCustomer = customerRepository.findById(id);
        if (existingCustomer.isEmpty()) {
            throw new NotFoundException(Message.MSG_111, id);
        }
        return modelMapper.map(existingCustomer.get(), CustomerDto.class);
    }

    public CustomerDto getByUsername(String username) {
        Optional<Customer> existingCustomer = customerRepository.findByUsername(username);
        if (existingCustomer.isEmpty()) {
            throw new NotFoundException(Message.MSG_111, username);
        }
        return modelMapper.map(existingCustomer.get(), CustomerDto.class);
    }

    public boolean switchStatus(String id) {
        Optional<Customer> existingCustomer = customerRepository.findById(id);
        if (existingCustomer.isPresent()) {
            Customer customer = existingCustomer.get();
            Status newStatus = customer.getStatus() == Status.ACTIVE ? Status.INACTIVE : Status.ACTIVE;
            customer.setStatus(newStatus);
            customerRepository.save(customer);
            return true;
        }
        throw new NotFoundException(Message.MSG_111, id);
    }

    @Override
    public void softDelete(String ids) {
        String[] idList = ids.split(",");
        List<Customer> customers = customerRepository.findAllById(Arrays.asList(idList));
        for (Customer customer : customers) {
            customer.setDeleted(true);
            List<InjectionResult> injectionResults = injectionResultRepository.findByCustomerId(customer.getId());
            injectionResults.forEach(ir -> ir.setDeleted(true));
            injectionResultRepository.saveAll(injectionResults);
        }
        customerRepository.saveAll(customers);
    }

    @Override
    public List<CustomerNameDto> getCustomerName() {
        List<Customer> customers = customerRepository.findAll();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return customers.stream().map(
                customer -> new CustomerNameDto(
                        customer.getId(),
                        String.format("%s - %s - %s", customer.getIdentityCard(), customer.getFullName(), customer.getDateOfBirth().format(dtf))
                )
        ).toList();
    }
}

