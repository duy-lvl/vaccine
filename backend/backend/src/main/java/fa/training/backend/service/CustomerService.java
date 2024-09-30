package fa.training.backend.service;

import fa.training.backend.dto.request.customer.CustomerAddRequest;
import fa.training.backend.dto.request.customer.CustomerUpdateRequest;
import fa.training.backend.dto.response.customer.CustomerDto;
import fa.training.backend.dto.response.customer.CustomerNameDto;
import fa.training.backend.dto.response.PageDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomerService {
    boolean add(CustomerAddRequest customerAddRequest);
    boolean update(CustomerUpdateRequest customerUpdateRequest, String id);
    PageDto<CustomerDto> getAll(Pageable pageable, String search);
    CustomerDto getById(String id);
    CustomerDto getByUsername(String username);
    boolean switchStatus(String id);
    void softDelete(String ids);

    List<CustomerNameDto> getCustomerName();
}