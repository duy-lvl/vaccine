package fa.training.frontend.controller;

import fa.training.frontend.dto.request.customer.CustomerReportRequest;
import fa.training.frontend.dto.request.vaccine.VaccineReportRequest;
import fa.training.frontend.dto.request.report.InjectionResultReportRequest;
import fa.training.frontend.dto.response.MonthlyData;
import fa.training.frontend.dto.response.PageDto;
import fa.training.frontend.dto.response.report.InjectionResultReportDto;
import fa.training.frontend.service.ReportService;
import fa.training.frontend.service.VaccineTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;
    @Autowired
    private VaccineTypeService vaccineTypeService;

    @GetMapping("/customers/chart")
    public String getCustomerChart(@RequestParam(required = false, defaultValue = "2024") int year,
                                   Model model) {
        var data = reportService.getCustomerChart(year);
        List<Integer> years = getYears();
        List<Integer> dataPoints = getDataPoints(data);

        model.addAttribute("dataPoints", dataPoints);
        model.addAttribute("selectedYear", year);
        model.addAttribute("years", years);
        return "report/customer-chart";
    }

    @GetMapping("/vaccines/chart")
    public String getVaccineChart(@RequestParam(required = false, defaultValue = "2024") int year,
                                  Model model) {
        var data = reportService.getVaccineChart(year);
        List<Integer> years = getYears();
        List<Integer> dataPoints = getDataPoints(data);

        model.addAttribute("dataPoints", dataPoints);
        model.addAttribute("selectedYear", year);
        model.addAttribute("years", years);
        return "report/vaccine-chart";
    }

    @GetMapping("/customers/statistics")
    public String getCustomerStatistics(
            @RequestParam(required = false, defaultValue = "") String dobFrom,
            @RequestParam(required = false, defaultValue = "") String dobTo,
            @RequestParam(required = false, defaultValue = "") String fullName,
            @RequestParam(required = false, defaultValue = "") String address,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "5") int size,
            Model model
    ) {
        CustomerReportRequest request = new CustomerReportRequest(dobFrom, dobTo, fullName, address, page, size);
        model.addAttribute("pageDto", reportService.getCustomerReport(request));
        model.addAttribute("request", request);

        return "report/customer-statistics";
    }


    @GetMapping("/vaccines/statistics")
    public String getVaccineStatistics(
            @RequestParam(required = false, defaultValue = "") String vaccineTypeId,
            @RequestParam(required = false, defaultValue = "") String nextInjectionFrom,
            @RequestParam(required = false, defaultValue = "") String nextInjectionTo,
            @RequestParam(required = false, defaultValue = "") String origin,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "5") int size,
            Model model
    ) {
        VaccineReportRequest request = new VaccineReportRequest(vaccineTypeId, nextInjectionFrom, nextInjectionTo, origin, page, size);
        var pageDto = reportService.getVaccineReport(request);


        model.addAttribute("pageDto", pageDto);
        model.addAttribute("request", request);

        return "report/vaccine-statistics";
    }


    @GetMapping("/injection-results/statistics")
    public String getInjectionResultStatistics(
            @RequestParam(required = false, defaultValue = "") String vaccineTypeId,
            @RequestParam(required = false, defaultValue = "") LocalDate injectionDateFrom,
            @RequestParam(required = false, defaultValue = "") LocalDate injectionDateTo,
            @RequestParam(required = false, defaultValue = "") String vaccineId,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "5") int size,
            Model model) {
        InjectionResultReportRequest request = new InjectionResultReportRequest(injectionDateFrom, injectionDateTo, vaccineTypeId, vaccineId, page, size);
        // Retrieve the current page of employees based on the given parameters
        PageDto<InjectionResultReportDto> pageDto = reportService.getInjectionResultReport(request);
        model.addAttribute("size", size);
        model.addAttribute("page", page);
        model.addAttribute("totalPages", pageDto.getTotalPages());
        // Calculate the starting and ending index for the current page
        int start = pageDto.getData().isEmpty() ? 0 : (page - 1) * size + 1; // Calculate starting index
        int end = Math.min(page * size, pageDto.getTotalElements()); // Calculate ending index
        model.addAttribute("start", start);
        model.addAttribute("end", end);
        model.addAttribute("totalElements", pageDto.getTotalElements());
        //
        model.addAttribute("vaccineTypes", vaccineTypeService.getVaccineTypes(false));
        model.addAttribute("pageDto", pageDto);
        model.addAttribute("request", request);
        return "report/injection-result";
    }

    private List<Integer> getYears() {
        List<Integer> years = new ArrayList<>();
        for (int i = 2018; i <= LocalDate.now().getYear() + 1; i++) {
            years.add(i);
        }
        return years;
    }

    private List<Integer> getDataPoints(List<MonthlyData> data) {
        List<Integer> dataPoints = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            dataPoints.add(0);
        }
        for (var d : data) {
            dataPoints.set(d.getMonth().getValue() - 1, d.getAmount());
        }
        return dataPoints;
    }
}
