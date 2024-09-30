package fa.training.backend.service.impl;

import fa.training.backend.dto.request.customer.CustomerReportRequest;
import fa.training.backend.dto.request.injection_result.InjectionResultReportRequest;
import fa.training.backend.dto.request.vaccine.VaccineReportRequest;
import fa.training.backend.dto.response.*;
import fa.training.backend.dto.response.customer.CustomerReportDto;
import fa.training.backend.dto.response.injection_result.InjectionResultReportDto;
import fa.training.backend.dto.response.vaccine.VaccineReportDto;
import fa.training.backend.repository.CustomerRepository;
import fa.training.backend.repository.InjectionResultRepository;
import fa.training.backend.repository.VaccineRepository;
import fa.training.backend.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {
    private final CustomerRepository customerRepository;
    private final InjectionResultRepository injectionResultRepository;
    private final VaccineRepository vaccineRepository;

    @Autowired
    public ReportServiceImpl(CustomerRepository customerRepository, InjectionResultRepository injectionResultRepository, VaccineRepository vaccineRepository) {
        this.customerRepository = customerRepository;
        this.injectionResultRepository = injectionResultRepository;
        this.vaccineRepository = vaccineRepository;
    }

    @Override
    public List<MonthlyData> getCustomerMonthlyReport(int year) {
        List<Integer[]> data = injectionResultRepository.findByYear(year);
        List<MonthlyData> results = new ArrayList<>();
        for (Integer[] result : data) {
            results.add(new MonthlyData(
                    Month.of(result[0]),
                    result[1]
            ));
        }
        return results;
    }

    @Override
    public List<MonthlyData> getVaccineMonthlyReport(int year) {
        List<Integer[]> data = injectionResultRepository.findVaccineInjectedByYear(year);
        List<MonthlyData> results = new ArrayList<>();
        for (Integer[] result : data) {
            results.add(new MonthlyData(
                    Month.of(result[0]),
                    result[1]
            ));
        }
        return results;
    }

    @Override
    public PageDto<CustomerReportDto> getCustomerStatistics(CustomerReportRequest request) {
        Page<Object[]> data = injectionResultRepository.findByDateOfBirthBetweenAndFullNameAndAddress(
                request.getDobFrom(),
                request.getDobTo(),
                request.fullName(),
                request.address(),
                request.getPageable()
        );
        List<CustomerReportDto> results = new ArrayList<>();
        for (Object[] item : data.getContent()) {
            results.add(new CustomerReportDto(
                    String.valueOf(item[0]),
                    LocalDate.parse(String.valueOf(item[1])),
                    String.valueOf(item[2]),
                    String.valueOf(item[3]),
                    Long.parseLong(String.valueOf(item[4]))
            ));
        }
        return new PageDto<>(
                request.getPage(),
                request.getSize(),
                data.getTotalPages(),
                data.getTotalElements(),
                results
        );
    }

    @Override
    public PageDto<VaccineReportDto> getVaccineStatistics(VaccineReportRequest request) {

        List<VaccineReportDto> vaccineReportDtos = new ArrayList<>();
        Page<Object[]> data = injectionResultRepository.findByNextInjectionDateBetweenAndVaccineTypeIdAndOrigin(
                request.getNextInjectionFrom(),
                request.getNextInjectionTo(),
                request.getVaccineTypeId(),
                request.getOrigin(),
                request.getPageable()
        );
        for (Object[] item : data.getContent()) {
            vaccineReportDtos.add(new VaccineReportDto(
                    String.valueOf(item[0]),
                    String.valueOf(item[1]),
                    Long.parseLong(String.valueOf(item[2])),
                    LocalDate.parse(String.valueOf(item[3])),
                    LocalDate.parse(String.valueOf(item[4])),
                    String.valueOf(item[5])
            ));
        }
        return new PageDto<>(
                request.getPage(),
                request.getSize(),
                data.getTotalPages(),
                data.getTotalElements(),
                vaccineReportDtos
        );

    }

    @Override
    public PageDto<InjectionResultReportDto> getInjectionResult(InjectionResultReportRequest request) {
        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize());
        Page<Object[]> injectionResultPage = injectionResultRepository
                .findByInjectionDateBetweenOrVaccineIdOrVaccineVaccineTypeId(
                        request.getInjectionDateFrom(),
                        request.getInjectionDateTo(),
                        request.getVaccineTypeId(),
                        request.getVaccineId(),
                        pageable
                );
        return new PageDto<>(
                request.getPage(),
                request.getSize(),
                injectionResultPage.getTotalPages(),
                injectionResultPage.getTotalElements(),
                injectionResultPage.stream().map(ir -> InjectionResultReportDto
                        .builder()
                        .vaccineTypeName(String.valueOf(ir[0]))
                        .vaccineName(String.valueOf(ir[1]))
                        .customerName(String.valueOf(ir[2]))
                        .dateOfInjection(LocalDate.parse(String.valueOf(ir[3])))
                        .numberOfInjection(Integer.parseInt(String.valueOf(ir[4])))
                        .build()
                ).toList()
        );
    }
}
