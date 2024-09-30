package fa.training.frontend.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import fa.training.frontend.dto.request.vaccine_type.NewVaccineTypeRequest;
import fa.training.frontend.dto.response.*;
import fa.training.frontend.dto.response.vaccine_type.VaccineTypeNameDto;
import fa.training.frontend.dto.response.vaccine_type.VaccineTypeResponseDto;
import fa.training.frontend.service.endpoint.FileEndpoint;
import fa.training.frontend.service.endpoint.VaccineTypeEndpoint;
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
import java.util.List;

@Service
public class VaccineTypeService {

    private final VaccineTypeEndpoint vaccineTypeEndpoint;
    private final FileEndpoint fileEndpoint;
    private final Gson gson;

    @Autowired
    public VaccineTypeService(VaccineTypeEndpoint vaccineTypeEndpoint, FileEndpoint fileEndpoint, Gson gson) {
        this.vaccineTypeEndpoint = vaccineTypeEndpoint;
        this.fileEndpoint = fileEndpoint;
        this.gson = gson;
    }

    // *** CREATE Methods ***

    public String add(NewVaccineTypeRequest request, BindingResult bindingResult, MultipartFile image) throws IOException {
        HttpServiceBase<NewVaccineTypeRequest, Response> addHttpServiceBase = new HttpServiceBase<>();
        Response response = addHttpServiceBase.postToAPI(request, vaccineTypeEndpoint.add(), Response.class);

        if (response.getData() == null) {
            if (response.getCode() == Message.MSG_123.getCode()) {
                bindingResult.rejectValue("code", "error.code", Message.MSG_123.getMessage());
            }
            if (response.getCode() == Message.MSG_124.getCode()) {
                bindingResult.rejectValue("name", "error.name", Message.MSG_124.getMessage());
            }
            return "Error in adding vaccine type";
        }

        if (response.isSuccessful() && !image.isEmpty()) {
            String id = response.getData().toString();
            String imageUrl = uploadImage(image);
            HttpServiceBase<String, Response> updateHttpServiceBase = new HttpServiceBase<>();
            updateHttpServiceBase.putToAPI(imageUrl, vaccineTypeEndpoint.updateImage(id), Response.class);
        }

        return response.getDescription();
    }

    // *** READ Methods ***

    public PageDto<VaccineTypeResponseDto> getAllVaccineTypes(int page, int size, String search) {
        HttpServiceBase<Object, Response> httpServiceBase = new HttpServiceBase<>();
        Response response = httpServiceBase.getFromAPI(vaccineTypeEndpoint.getAll(page, size, search), Response.class);
        Type pageDtoType = new TypeToken<PageDto<VaccineTypeResponseDto>>() {}.getType();
        return gson.fromJson(gson.toJson(response.getData()), pageDtoType);
    }

    public VaccineTypeResponseDto getVaccineTypeById(String id) {
        HttpServiceBase<Object, Response> httpServiceBase = new HttpServiceBase<>();
        Response response = httpServiceBase.getFromAPI(vaccineTypeEndpoint.getById(id), Response.class);
        if (response != null && response.getData() != null) {
            return gson.fromJson(gson.toJson(response.getData()), VaccineTypeResponseDto.class);
        }
        return null;
    }

    public List<VaccineTypeNameDto> getVaccineTypes(boolean isActive) {
        HttpServiceBase<Void, Response> httpServiceBase = new HttpServiceBase<>();
        Response response = httpServiceBase.getFromAPI(vaccineTypeEndpoint.getVaccineTypeName(isActive), Response.class);
        String json = gson.toJson(response.getData());
        return gson.fromJson(json, new TypeToken<List<VaccineTypeNameDto>>() {}.getType());
    }

    // *** UPDATE Methods ***

    public boolean updateVaccineType(
            VaccineTypeResponseDto vaccineTypeResponseDto,
            String id,
            MultipartFile image,
            boolean isUpdatingImage,
            BindingResult bindingResult,
            Response responseWrapper
    ) throws IOException {
        HttpServiceBase<VaccineTypeResponseDto, Response> httpServiceBase = new HttpServiceBase<>();
        Response response = httpServiceBase.putToAPI(vaccineTypeResponseDto, vaccineTypeEndpoint.update(id), Response.class);

        if (response.getCode() == Message.MSG_124.getCode()) {
            bindingResult.rejectValue("name", "error.name", Message.MSG_124.getMessage());
        }
        responseWrapper.setDescription(response.getDescription());
        responseWrapper.setData(response.getData());

        if (isUpdatingImage && !image.isEmpty()) {
            String imageUrl = uploadImage(image);
            HttpServiceBase<String, Response> updateHttpServiceBase = new HttpServiceBase<>();
            updateHttpServiceBase.putToAPI(imageUrl, vaccineTypeEndpoint.updateImage(id), Response.class);
        }
        return true;
    }

    // *** DELETE Methods ***

    public void deleteVaccineType(String id) {
        HttpServiceBase<Object, Response> httpServiceBase = new HttpServiceBase<>();
        httpServiceBase.deleteFromAPI(vaccineTypeEndpoint.delete(id), Response.class);
    }

    public Response makeInActive(String ids) {
        HttpServiceBase<String, Response> httpServiceBase = new HttpServiceBase<>();
        Response response = httpServiceBase.putToAPI(ids, vaccineTypeEndpoint.makeInActive(), Response.class);
        return response;
    }

    public Response makeActive(String ids) {
        HttpServiceBase<String, Response> httpServiceBase = new HttpServiceBase<>();
        Response response = httpServiceBase.putToAPI(ids, vaccineTypeEndpoint.makeActive(), Response.class);
        return response;
    }

    // *** Helper Methods ***

    private String uploadImage(MultipartFile image) throws IOException {
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
}
