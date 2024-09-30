package fa.training.frontend.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import fa.training.frontend.dto.request.injection_reasult.InjectionResultAddRequest;
import fa.training.frontend.dto.request.injection_reasult.InjectionResultRequest;
import fa.training.frontend.dto.request.injection_reasult.InjectionResultDto;
import fa.training.frontend.dto.response.*;
import fa.training.frontend.dto.response.customer.CustomerNameDto;
import fa.training.frontend.dto.response.injection_result.InjectionResultDetailDto;
import fa.training.frontend.dto.response.injection_schedule.InjectionScheduleDto;
import fa.training.frontend.service.endpoint.InjectionResultEndpoint;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class InjectionResultService {
    @Autowired
    private InjectionResultEndpoint injectionResultEndpoint;

    @Autowired
    private Gson gson;

    @Autowired
    private ModelMapper mapper;
    @Autowired
    private VaccineTypeService vaccineTypeService;

    public boolean add(InjectionResultRequest request) {
        HttpServiceBase<InjectionResultRequest, Response> httpServiceBase = new HttpServiceBase<>();
        Response response = httpServiceBase.postToAPI(request, injectionResultEndpoint.add(), Response.class);
        return true;
    }

    public boolean update(InjectionResultDetailDto request, String id) {
        HttpServiceBase<InjectionResultDetailDto, Response> httpServiceBase = new HttpServiceBase<>();
        Response response = httpServiceBase.putToAPI(request, injectionResultEndpoint.update(id), Response.class);
        return Boolean.parseBoolean(response.getData().toString());
    }

    public InjectionResultDetailDto getById(String id) {
        HttpServiceBase<Void, Response> httpServiceBase = new HttpServiceBase<>();
        Response response = httpServiceBase.getFromAPI(injectionResultEndpoint.update(id), Response.class);
        String json = gson.toJson(response.getData());
        return gson.fromJson(json, InjectionResultDetailDto.class);
//        return mapper.map(dto, InjectionResultRequest.class);
    }

    public List<CustomerNameDto> getCustomerList() {
        HttpServiceBase<Void, Response> httpServiceBase = new HttpServiceBase<>();
        Response response = httpServiceBase.getFromAPI(injectionResultEndpoint.customerName(), Response.class);
        String json = gson.toJson(response.getData());
        return gson.fromJson(json, new TypeToken<List<CustomerNameDto>>(){}.getType());
    }



    public PageDto<InjectionResultDto> getAllInjectionResult(int page, int size, String search) {
        HttpServiceBase<Object, Response> httpServiceBase = new HttpServiceBase<>();
        Response response = httpServiceBase.getFromAPI(injectionResultEndpoint.getAll(page, size, search), Response.class);
        Type pageDtoType = new TypeToken<PageDto<InjectionResultDto>>() {
        }.getType();
        return gson.fromJson(gson.toJson(response.getData()), pageDtoType);
    }

    public boolean deleteInjectionResults(String idList){
        HttpServiceBase<Object, Response> httpServiceBase = new HttpServiceBase<>();

        Response response = httpServiceBase.deleteFromAPI(injectionResultEndpoint.delete(idList), Response.class);
        return Boolean.parseBoolean(response.getData().toString());
    }

    public PageDto<InjectionScheduleDto> createAndGetSchedule(InjectionResultAddRequest request, int page, int size) {
        HttpServiceBase<InjectionResultAddRequest, Response> httpServiceBase = new HttpServiceBase<>();
        Response response = httpServiceBase.getFromAPI(injectionResultEndpoint.getPageScheduleByRequest(page,size,request.toQuery()), Response.class);
        Type pageDtoType = new TypeToken<PageDto<InjectionScheduleDto>>() {}.getType();
        return gson.fromJson(gson.toJson(response.getData()), pageDtoType);
    }
}
