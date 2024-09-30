package fa.training.backend.service;

import fa.training.backend.dto.request.customer.CustomerReportRequest;
import fa.training.backend.dto.request.injection_result.InjectionResultReportRequest;
import fa.training.backend.dto.request.vaccine.VaccineReportRequest;
import fa.training.backend.dto.response.*;
import fa.training.backend.dto.response.customer.CustomerReportDto;
import fa.training.backend.dto.response.injection_result.InjectionResultReportDto;
import fa.training.backend.dto.response.vaccine.VaccineReportDto;

import java.util.List;

public interface ReportService {
    List<MonthlyData> getCustomerMonthlyReport(int year);
    List<MonthlyData> getVaccineMonthlyReport(int year);
    PageDto<CustomerReportDto> getCustomerStatistics(CustomerReportRequest request);
    PageDto<VaccineReportDto> getVaccineStatistics(VaccineReportRequest request);
    PageDto<InjectionResultReportDto> getInjectionResult(InjectionResultReportRequest request);
}
