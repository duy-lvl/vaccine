package fa.training.backend.controller;

import fa.training.backend.dto.request.customer.CustomerReportRequest;
import fa.training.backend.dto.request.injection_result.InjectionResultReportRequest;
import fa.training.backend.dto.request.vaccine.VaccineReportRequest;
import fa.training.backend.dto.response.*;
import fa.training.backend.dto.response.customer.CustomerReportDto;
import fa.training.backend.dto.response.injection_result.InjectionResultReportDto;
import fa.training.backend.dto.response.vaccine.VaccineReportDto;
import fa.training.backend.service.ReportService;
import fa.training.backend.util.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reports")
public class ReportController {

    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/customers")
    public ResponseEntity<Response<PageDto<CustomerReportDto>>> getCustomerStatistics(CustomerReportRequest request) {
        return ResponseEntity.ok()
                .body(new Response<>(
                        Message.MSG_282.getCode(),
                        Message.MSG_282.getMessage(),
                        reportService.getCustomerStatistics(request)
                ));
    }

    @GetMapping("/customers/{year}")
    public ResponseEntity<Response<List<MonthlyData>>> getCustomerStatisticsByYear(@PathVariable int year) {

        return ResponseEntity.ok()
                .body(new Response<>(
                        Message.MSG_282.getCode(),
                        Message.MSG_282.getMessage(),
                        reportService.getCustomerMonthlyReport(year)
                ));
    }

    @GetMapping("/vaccines")
    public ResponseEntity<Response<PageDto<VaccineReportDto>>> getVaccineStatistics(VaccineReportRequest request) {
        return ResponseEntity.ok()
                .body(new Response<>(
                        Message.MSG_253.getCode(),
                        Message.MSG_253.getMessage(),
                        reportService.getVaccineStatistics(request)
                ));
    }

    @GetMapping("/vaccines/{year}")
    public ResponseEntity<Response<List<MonthlyData>>> getVaccineStatisticsByYear(@PathVariable int year) {
        return ResponseEntity.ok()
                .body(new Response<>(
                        Message.MSG_253.getCode(),
                        Message.MSG_253.getMessage(),
                        reportService.getVaccineMonthlyReport(year)
                ));
    }
    @GetMapping("/injection-results")
    public ResponseEntity<Response<PageDto<InjectionResultReportDto>>> getInjectionResult(InjectionResultReportRequest request) {
        return ResponseEntity.ok()
                .body(new Response<>(
                        Message.MSG_281.getCode(),
                        Message.MSG_281.getMessage(),
                        reportService.getInjectionResult(request)
                ));
    }

}
