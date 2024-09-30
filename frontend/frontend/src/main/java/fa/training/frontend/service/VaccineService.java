package fa.training.frontend.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import fa.training.frontend.dto.request.vaccine.VaccineAddRequestDto;
import fa.training.frontend.dto.request.vaccine.VaccineInjectionScheduleRequestDto;
import fa.training.frontend.dto.request.vaccine.VaccineUpdateDto;

import fa.training.frontend.dto.response.PageDto;
import fa.training.frontend.dto.response.Response;
import fa.training.frontend.dto.response.employee.EmployeeListDto;
import fa.training.frontend.dto.response.vaccine.VaccineResponseDto;
import fa.training.frontend.dto.response.vaccine_type.VaccineTypeNameDto;
import fa.training.frontend.service.endpoint.VaccineEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

@Service
public class VaccineService {
    @Autowired
    private VaccineEndpoint vaccineEndpoint;

    @Autowired
    private Gson gson;

    public String addVaccine(VaccineAddRequestDto vaccineAddRequestDto, BindingResult bindingResult) {
        HttpServiceBase<VaccineAddRequestDto, Response> httpServiceBase = new HttpServiceBase<>();
        Response response = httpServiceBase.postToAPI(vaccineAddRequestDto, vaccineEndpoint.add(), Response.class);
        if (response.getData() == null) {
                        return null;
        }
        return response.getData().toString();
    }

//    public Response getAllVaccines(int page, int size, String query) {
//        HttpServiceBase<Object, Response> httpServiceBase = new HttpServiceBase<>();
//        Response response = httpServiceBase.getFromAPI(vaccineEndpoint.getAll(page, size, query), Response.class);
//        return response;
//    }

    public PageDto<VaccineResponseDto> getAllVaccines(int page, int size, String query) {
        HttpServiceBase<Object, Response> httpServiceBase = new HttpServiceBase<>();
        Response response = httpServiceBase.getFromAPI(vaccineEndpoint.getAll(page, size, query), Response.class);
        Type pageDtoType = new TypeToken<PageDto<VaccineResponseDto>>() {}.getType();
        return gson.fromJson(gson.toJson(response.getData()), pageDtoType);
    }

//    public PageDto<EmployeeListDto> getAllEmployee(int page, int size, String search) {
//        HttpServiceBase<Object, Response> httpServiceBase = new HttpServiceBase<>();
//        Response response = httpServiceBase.getFromAPI(employeeEndpoint.getAll(page, size, search), Response.class);
//        Type pageDtoType = new TypeToken<PageDto<EmployeeListDto>>() {}.getType();
//        return gson.fromJson(gson.toJson(response.getData()), pageDtoType);
//    }

    public List<VaccineInjectionScheduleRequestDto> getAllVaccineForInjectionSchedule() {
        HttpServiceBase<Object, Response> httpServiceBase = new HttpServiceBase<>();
        Response response = httpServiceBase.getFromAPI(vaccineEndpoint.getVaccineGetAllForInjectionSchedule(), Response.class);
        String json = gson.toJson(response.getData());
        Type type = new TypeToken<List<VaccineInjectionScheduleRequestDto>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public List<VaccineTypeNameDto> getVaccineTypes() {
        HttpServiceBase<Void, Response> httpServiceBase = new HttpServiceBase<>();
        Response response = httpServiceBase.getFromAPI(vaccineEndpoint.vaccineType(), Response.class);
        String json = gson.toJson(response.getData());
        return gson.fromJson(json, new TypeToken<List<VaccineTypeNameDto>>(){}.getType());
    }
    public Response  makeInActive(String ids) {
        HttpServiceBase<String, Response> httpServiceBase = new HttpServiceBase<>();


        String url = vaccineEndpoint.makeInActive();
        Response response = httpServiceBase.putToAPI(ids, url, Response.class);

        return response;

    }

    public VaccineUpdateDto getVaccineByIdForUpdate(String id) {
        HttpServiceBase<VaccineUpdateDto, Response> httpServiceBase = new HttpServiceBase<>();
        Response response = httpServiceBase.getFromAPI(vaccineEndpoint.getVaccineByIdForUpdate(id), Response.class);
        String json = gson.toJson(response.getData());
        Type type = new TypeToken<VaccineUpdateDto>() {
        }.getType();
        return gson.fromJson(json, type);
    }


    public boolean updateVaccine(VaccineUpdateDto vaccineUpdateDto,BindingResult bindingResult) {
        HttpServiceBase<VaccineUpdateDto, Response> httpServiceBase = new HttpServiceBase<>();
        Response response = httpServiceBase.putToAPI(vaccineUpdateDto, vaccineEndpoint.update(vaccineUpdateDto.getId()), Response.class);
        if (response.getData() == null) {
            return false;
        }
        return (boolean) response.getData();
    }

    public byte[] downloadTemplate() {
        HttpServiceBase<Void, byte[]> httpServiceBase = new HttpServiceBase<>();
        return httpServiceBase.getFromAPI(vaccineEndpoint.template(), byte[].class);

    }

    public Response importVaccine(MultipartFile file) throws IOException {
        HttpServiceBase<MultipartFile, Response> httpServiceBase = new HttpServiceBase<>();

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = httpServiceBase.getHttpHeader();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        });
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        Response response;
        try {
            response = restTemplate.exchange(
                    vaccineEndpoint.importVaccine(),
                    HttpMethod.POST,
                    requestEntity,
                    Response.class
            ).getBody();
        } catch (HttpClientErrorException e) {
            response = e.getResponseBodyAs(Response.class);
        }

        return response;

    }
}
