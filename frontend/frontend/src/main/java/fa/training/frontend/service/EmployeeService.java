package fa.training.frontend.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import fa.training.frontend.dto.request.employee.EmployeeRequest;
import fa.training.frontend.dto.request.employee.EmployeeUpdateDto;
import fa.training.frontend.dto.response.employee.EmployeeListDto;
import fa.training.frontend.dto.response.PageDto;
import fa.training.frontend.dto.response.Response;
import fa.training.frontend.service.endpoint.EmployeeEndpoint;
import fa.training.frontend.service.endpoint.FileEndpoint;
import fa.training.frontend.util.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.Type;

@Service
public class EmployeeService {

    private final EmployeeEndpoint employeeEndpoint;
    private final Gson gson;
    private final FileEndpoint fileEndpoint;

    @Autowired
    public EmployeeService(EmployeeEndpoint employeeEndpoint, FileEndpoint fileEndPoint, Gson gson) {
        this.employeeEndpoint = employeeEndpoint;
        this.fileEndpoint = fileEndPoint;
        this.gson = gson;
    }

    public PageDto<EmployeeListDto> getAllEmployee(int page, int size, String search) {
        HttpServiceBase<Object, Response> httpServiceBase = new HttpServiceBase<>();
        Response response = httpServiceBase.getFromAPI(employeeEndpoint.getAll(page, size, search), Response.class);
        Type pageDtoType = new TypeToken<PageDto<EmployeeListDto>>() {}.getType();
        return gson.fromJson(gson.toJson(response.getData()), pageDtoType);
    }

    public EmployeeUpdateDto getEmployeeById(String id) {
        HttpServiceBase<Object, Response> httpServiceBase = new HttpServiceBase<>();
        String url = employeeEndpoint.getById(id);
        Response response = httpServiceBase.getFromAPI(url, Response.class);
        if (response != null && response.getData() != null) {
            return gson.fromJson(gson.toJson(response.getData()), EmployeeUpdateDto.class);
        }
        return null;
    }

    public boolean addEmployee(EmployeeRequest employeeRequest, BindingResult bindingResult, MultipartFile image) throws IOException {
        String imgUrl = null;

        // Upload image if provided
        if (!image.isEmpty()) {
            imgUrl = uploadAvatar(image);
        }

        employeeRequest.setUrlOfImage(imgUrl);
        HttpServiceBase<EmployeeRequest, Response> addHttpServiceBase = new HttpServiceBase<>();
        Response response = addHttpServiceBase.postToAPI(employeeRequest, employeeEndpoint.add(), Response.class);

        // Handle response errors
        if (response.getData() == null) {
            return handleErrorResponse(response, bindingResult);
        }
        return Boolean.parseBoolean(response.getData().toString());
    }

    // CRUD Operation: Update
    public boolean updateEmployee(String id, EmployeeUpdateDto employeeUpdateDto, MultipartFile imageFile, boolean isUpdating, BindingResult bindingResult) throws IOException {
        // Upload new image if updating
        if (isUpdating && !imageFile.isEmpty()) {
            String imageUrl = uploadAvatar(imageFile);
            if (imageUrl != null) {
                employeeUpdateDto.setImage(imageUrl);
            }
        }
        HttpServiceBase<EmployeeUpdateDto, Response> postHttpServiceBase = new HttpServiceBase<>();
        Response response = postHttpServiceBase.putToAPI(employeeUpdateDto, employeeEndpoint.update(id), Response.class);

        // Handle response errors
        return handleErrorResponse(response, bindingResult);
    }

    // CRUD Operation: Delete
    public boolean deleteEmployee(String idList) {
        HttpServiceBase<Object, Response> httpServiceBase = new HttpServiceBase<>();
        Response response = httpServiceBase.deleteFromAPI(employeeEndpoint.delete(idList), Response.class);
        return Boolean.parseBoolean(response.getData().toString());
    }

    // Helper method to upload an avatar image
    private String uploadAvatar(MultipartFile image) throws IOException {
        HttpServiceBase<MultipartFile, Response> imgHttp = new HttpServiceBase<>();
        String imgHttpUrl = fileEndpoint.fileUpload();
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = imgHttp.getHttpHeader();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new ByteArrayResource(image.getBytes()) {
            @Override
            public String getFilename() {
                return image.getOriginalFilename();
            }
        });
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<Response> responseEntity = restTemplate.exchange(
                imgHttpUrl,
                HttpMethod.POST,
                requestEntity,
                Response.class
        );
        Response response = responseEntity.getBody();
        return response.getData().toString();
    }

    // Private method to handle error responses
    private boolean handleErrorResponse(Response response, BindingResult bindingResult) {
        if (Message.MSG_162.getCode() == response.getCode()) {
            bindingResult.rejectValue("phone", "error.phone", Message.MSG_162.getMessage());
            return false;
        } else if (Message.MSG_164.getCode() == response.getCode()) {
            bindingResult.rejectValue("email", "error.email", Message.MSG_164.getMessage());
            return false;
        } else if (Message.MSG_163.getCode() == response.getCode()) {
            bindingResult.rejectValue("username", "username.email", Message.MSG_163.getMessage());
            return false;
        }
        return true; // No error
    }
}
