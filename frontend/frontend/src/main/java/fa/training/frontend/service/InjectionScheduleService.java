package fa.training.frontend.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import fa.training.frontend.dto.request.injection_schedule.InjectionScheduleUpdateRequest;
import fa.training.frontend.dto.request.injection_schedule.InjectionScheduleAddRequest;
import fa.training.frontend.dto.response.injection_schedule.InjectionScheduleDetailDto;
import fa.training.frontend.dto.response.Response;
import fa.training.frontend.dto.response.*;
import fa.training.frontend.dto.response.injection_schedule.InjectionScheduleDto;
import fa.training.frontend.dto.response.vaccine.VaccineNameDto;
import fa.training.frontend.dto.response.vaccine_type.VaccineTypeNameDto;
import fa.training.frontend.service.endpoint.InjectionScheduleEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InjectionScheduleService {

    private final InjectionScheduleEndpoint injectionScheduleEndpoint;
    private final Gson gson;

    @Autowired
    public InjectionScheduleService(InjectionScheduleEndpoint injectionScheduleEndpoint, Gson gson) {
        this.injectionScheduleEndpoint = injectionScheduleEndpoint;
        this.gson = gson;
    }

    public InjectionScheduleDetailDto getInjectionScheduleById(String id) {
        HttpServiceBase<Object, Response> httpServiceBase = new HttpServiceBase<>();
        Response response = httpServiceBase.getFromAPI(injectionScheduleEndpoint.getById(id), Response.class);
        String json = gson.toJson(response.getData());
        return gson.fromJson(json, InjectionScheduleDetailDto.class);
    }

    public List<VaccineNameDto> getVaccines() {
        HttpServiceBase<Void, Response> httpServiceBase = new HttpServiceBase<>();
        Response response = httpServiceBase.getFromAPI(injectionScheduleEndpoint.vaccine(), Response.class);
        String json = gson.toJson(response.getData());
        return gson.fromJson(json, new TypeToken<List<VaccineTypeNameDto>>(){}.getType());
    }

    public boolean updateInjectionSchedule(InjectionScheduleUpdateRequest injectionScheduleUpdateRequest, String id) {
        HttpServiceBase<InjectionScheduleUpdateRequest, Response> httpServiceBase = new HttpServiceBase<>();
        Response response = httpServiceBase.putToAPI(injectionScheduleUpdateRequest, injectionScheduleEndpoint.update(id), Response.class);
        return (boolean) response.getData();
    }

    public boolean saveInjectionSchedule(InjectionScheduleAddRequest injectionScheduleAddRequest) {
        HttpServiceBase<InjectionScheduleAddRequest, Response> addHttpServiceBase = new HttpServiceBase<>();
        Response response = addHttpServiceBase.postToAPI(injectionScheduleAddRequest, injectionScheduleEndpoint.add(), Response.class);
        return Boolean.parseBoolean(response.getData().toString());
    }
    public PageDto<InjectionScheduleDto> getList(int page, int size, String search) {
        HttpServiceBase<Void, Response> httpServiceBase = new HttpServiceBase<>();
        Response response = httpServiceBase.getFromAPI(injectionScheduleEndpoint.getAll(page, size, search), Response.class);
        String json = gson.toJson(response.getData());
        return gson.fromJson(json, new TypeToken<PageDto<InjectionScheduleDto>>(){}.getType());
    }
//    public PageDto<InjectionScheduleF>
}
