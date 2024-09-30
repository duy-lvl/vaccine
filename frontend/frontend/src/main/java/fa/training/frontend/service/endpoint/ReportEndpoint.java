package fa.training.frontend.service.endpoint;


import fa.training.frontend.dto.request.customer.CustomerReportRequest;
import fa.training.frontend.dto.request.vaccine.VaccineReportRequest;
import fa.training.frontend.dto.request.report.InjectionResultReportRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ReportEndpoint {
    @Autowired
    private Endpoint endpoint;

    private static final String CUSTOMER_CHART = "reports.customer.chart";
    private static final String CUSTOMER_STATISTICS = "reports.customer.statistics";
    private static final String VACCINE_CHART = "reports.vaccine.chart";
    private static final String VACCINE_STATISTICS = "reports.vaccine.statistics";
    private static final String INJECTION_RESULT_STATISTICS = "reports.injection-result.statistics";


    public String getCustomerChart(int year) {
        return String.format(endpoint.getUrl(CUSTOMER_CHART), year);
    }

    public String getVaccineChart(int year) {
        return String.format(endpoint.getUrl(VACCINE_CHART), year);
    }

    public String getCustomerStatistics(CustomerReportRequest request) {

        return String.format(
                endpoint.getUrl(CUSTOMER_STATISTICS),
                request.getDobFrom(),
                request.getDobTo(),
                request.getFullName(),
                request.getAddress(),
                request.getPage(),
                request.getSize()
        );
    }

    public String getVaccineStatistics(VaccineReportRequest request) {

        return String.format(
                endpoint.getUrl(VACCINE_STATISTICS),
                request.getVaccineTypeId(),
                request.getNextInjectionFrom(),
                request.getNextInjectionTo(),
                request.getOrigin(),
                request.getPage(),
                request.getSize()
        );
    }

    public String getInjectionResultStatistics(InjectionResultReportRequest request) {

        return String.format(
                endpoint.getUrl(INJECTION_RESULT_STATISTICS),
                request.getInjectionDateFrom() == null ? "" : request.getInjectionDateFrom(),
                request.getInjectionDateTo() == null ? "" : request.getInjectionDateTo(),
                request.getVaccineTypeId(),
                request.getVaccineId(),
                request.getPage(),
                request.getSize()
        );
    }
}
