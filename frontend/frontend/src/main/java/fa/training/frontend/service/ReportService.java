package fa.training.frontend.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import fa.training.frontend.dto.request.customer.CustomerReportRequest;
import fa.training.frontend.dto.request.vaccine.VaccineReportRequest;
import fa.training.frontend.dto.request.report.InjectionResultReportRequest;
import fa.training.frontend.dto.response.*;
import fa.training.frontend.dto.response.customer.CustomerReportDto;
import fa.training.frontend.dto.response.report.InjectionResultReportDto;
import fa.training.frontend.dto.response.vaccine.VaccineReportDto;
import fa.training.frontend.service.endpoint.ReportEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class ReportService {
    @Autowired
    private ReportEndpoint endpoint;

    @Autowired
    private Gson gson;

    public List<MonthlyData> getCustomerChart(int year) {
        HttpServiceBase<Void, Response> httpServiceBase = new HttpServiceBase<>();
        Response response = httpServiceBase.getFromAPI(endpoint.getCustomerChart(year), Response.class);
        Type type = new TypeToken<List<MonthlyData>>(){}.getType();
        String json = gson.toJson(response.getData());
        return gson.fromJson(json, type);

    }

    public List<MonthlyData> getVaccineChart(int year) {
        HttpServiceBase<Void, Response> httpServiceBase = new HttpServiceBase<>();
        Response response = httpServiceBase.getFromAPI(endpoint.getVaccineChart(year), Response.class);
        Type type = new TypeToken<List<MonthlyData>>(){}.getType();
        String json = gson.toJson(response.getData());
        return gson.fromJson(json, type);
    }

    public PageDto<CustomerReportDto> getCustomerReport(CustomerReportRequest request) {
        HttpServiceBase<CustomerReportRequest, Response> httpServiceBase = new HttpServiceBase<>();
        Response response = httpServiceBase.getFromAPI(endpoint.getCustomerStatistics(request), Response.class);
        Type type = new TypeToken<PageDto<CustomerReportDto>>(){}.getType();
        String json = gson.toJson(response.getData());
        return gson.fromJson(json, type);

    }

    public PageDto<CustomerReportDto> getVaccineReport(VaccineReportRequest request) {
        HttpServiceBase<CustomerReportRequest, Response> httpServiceBase = new HttpServiceBase<>();
        Response response = httpServiceBase.getFromAPI(endpoint.getVaccineStatistics(request), Response.class);
        Type type = new TypeToken<PageDto<VaccineReportDto>>(){}.getType();
        String json = gson.toJson(response.getData());
        return gson.fromJson(json, type);

    }

    public PageDto<InjectionResultReportDto> getInjectionResultReport(InjectionResultReportRequest request) {
        HttpServiceBase<InjectionResultReportRequest, Response> httpServiceBase = new HttpServiceBase<>();
        Response response = httpServiceBase.getFromAPI(endpoint.getInjectionResultStatistics(request), Response.class);
        Type type = new TypeToken<PageDto<InjectionResultReportDto>>(){}.getType();
        String json = gson.toJson(response.getData());
        return gson.fromJson(json, type);
    }
}
