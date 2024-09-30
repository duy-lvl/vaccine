package fa.training.frontend.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import fa.training.frontend.dto.request.customer.CustomerAddRequestDto;
import fa.training.frontend.dto.request.customer.CustomerUpdateRequestDto;
import fa.training.frontend.dto.response.customer.CustomerResponseDto;
import fa.training.frontend.dto.response.PageDto;
import fa.training.frontend.dto.response.Response;
import fa.training.frontend.service.endpoint.CustomerEndpoint;
import fa.training.frontend.util.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

@Service
public class CustomerService {

    private final CustomerEndpoint customerEndpoint;
    private final Gson gson;

    @Autowired
    public CustomerService(CustomerEndpoint customerEndpoint, Gson gson) {
        this.customerEndpoint = customerEndpoint;
        this.gson = gson;
    }

    public String addAndGetId(CustomerAddRequestDto customerAddRequestDto, BindingResult bindingResult) {
        HttpServiceBase<CustomerAddRequestDto, Response> httpServiceBase = new HttpServiceBase<>();

        Response response = httpServiceBase.postToAPI(customerAddRequestDto, customerEndpoint.add(), Response.class);

        if (response.getData() == null) {
            bindingResult.reject("add.customer", "add.customer");

            if (response.getCode() == Message.MSG_112.getCode()) {
                bindingResult.rejectValue("identityCard", "error.identityCard", Message.MSG_112.getMessage());
            }

            if (response.getCode() == Message.MSG_115.getCode()) {
                bindingResult.rejectValue("email", "error.email", Message.MSG_115.getMessage());
            }

            if (response.getCode() == Message.MSG_113.getCode()) {
                bindingResult.rejectValue("phone", "error.phone", Message.MSG_113.getMessage());
            }

            if (response.getCode() == Message.MSG_114.getCode()) {
                bindingResult.rejectValue("username", "error.username", Message.MSG_114.getMessage());
            }
            return null;
        }
        return response.getData().toString();
    }

    public boolean updateCustomer(CustomerUpdateRequestDto customerUpdateRequestDto, String id,BindingResult bindingResult) {
        HttpServiceBase<CustomerUpdateRequestDto, Response> httpServiceBase = new HttpServiceBase<>();
        Response response =  httpServiceBase.putToAPI(customerUpdateRequestDto, customerEndpoint.update(id), Response.class);
        if (response.getData() == null) {
            bindingResult.reject("add.customer", "add.customer");

            if (response.getCode() == Message.MSG_112.getCode()) {
                bindingResult.rejectValue("identityCard", "error.identityCard", Message.MSG_112.getMessage());
            }

            if (response.getCode() == Message.MSG_115.getCode()) {
                bindingResult.rejectValue("email", "error.email", Message.MSG_115.getMessage());
            }

            if (response.getCode() == Message.MSG_113.getCode()) {
                bindingResult.rejectValue("phone", "error.phone", Message.MSG_113.getMessage());
            }

            if (response.getCode() == Message.MSG_114.getCode()) {
                bindingResult.rejectValue("username", "error.username", Message.MSG_114.getMessage());
            }
            return false;
        }
        return true;
    }

    public PageDto<CustomerResponseDto> getAllCustomers(int page, int size, String query) {
        HttpServiceBase<Object, Response> httpServiceBase = new HttpServiceBase<>();
        Response response = httpServiceBase.getFromAPI(customerEndpoint.getAll(page, size, query), Response.class);
        String json = gson.toJson(response.getData());
        return gson.fromJson(json, new TypeToken<PageDto<CustomerResponseDto>>() {
        }.getType());
    }

    
    public CustomerResponseDto getCustomerById(String id) {
        HttpServiceBase<Object, Response> httpServiceBase = new HttpServiceBase<>();
        Response response = httpServiceBase.getFromAPI(customerEndpoint.getById(id), Response.class);
        String json = gson.toJson(response.getData());
        return gson.fromJson(json, CustomerResponseDto.class);
    }

    public void deleteCustomer(String id) {
        HttpServiceBase<Object, Response> httpServiceBase = new HttpServiceBase<>();
        httpServiceBase.deleteFromAPI(customerEndpoint.delete(id), Response.class);
    }
}
