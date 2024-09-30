package fa.training.backend.service.impl;

import fa.training.backend.dto.request.injection_result.InjectionResultRequest;
import fa.training.backend.dto.response.PageDto;
import fa.training.backend.dto.response.injection_result.InjectionResultDetailDto;
import fa.training.backend.dto.response.injection_result.InjectionResultDto;
import fa.training.backend.exception.common.InvalidStatusException;
import fa.training.backend.exception.common.NotFoundException;
import fa.training.backend.exception.injection_result.InsufficientInjectionException;
import fa.training.backend.model.Customer;
import fa.training.backend.model.InjectionResult;
import fa.training.backend.model.Vaccine;
import fa.training.backend.repository.CustomerRepository;
import fa.training.backend.repository.InjectionResultRepository;
import fa.training.backend.repository.InjectionScheduleRepository;
import fa.training.backend.repository.VaccineRepository;
import fa.training.backend.service.InjectionResultService;
import fa.training.backend.util.Message;
import fa.training.backend.util.Status;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class InjectionResultServiceImpl implements InjectionResultService {
    private final InjectionResultRepository injectionResultRepository;
    private final VaccineRepository vaccineRepository;
    private final CustomerRepository customerRepository;
    @Autowired
    private InjectionScheduleRepository injectionScheduleRepository;
    private ModelMapper mapper;
    private static final Logger logger = LoggerFactory.getLogger(InjectionResultServiceImpl.class);


    @Autowired
    public InjectionResultServiceImpl(InjectionResultRepository injectionResultRepository, VaccineRepository vaccineRepository, CustomerRepository customerRepository, ModelMapper mapper) {
        this.injectionResultRepository = injectionResultRepository;
        this.vaccineRepository = vaccineRepository;
        this.customerRepository = customerRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional(rollbackFor = {InsufficientInjectionException.class, SQLException.class})
    public boolean add(InjectionResultRequest request) {

        Optional<Vaccine> vaccineOptional = vaccineRepository.findByName(request.getVaccineId());
        if (vaccineOptional.isEmpty()) {
            throw new NotFoundException(Message.MSG_153, request.getVaccineId());
        }

        Optional<Customer> customerOptional = customerRepository.findById(request.getCustomerId());
        if (customerOptional.isEmpty()) {
            throw new NotFoundException(Message.MSG_111, request.getCustomerId());
        }

        Vaccine vaccine = vaccineOptional.get();
        Customer customer = customerOptional.get();

        if (!Status.ACTIVE.equals(customer.getStatus())) {
            throw new InvalidStatusException(Message.MSG_116);
        }
        if (vaccine.getNumberOfInjection() < request.getNumberOfInjection()) {
            throw new InsufficientInjectionException(Message.MSG_181);
        }

        vaccine.setNumberOfInjection(vaccine.getNumberOfInjection() - request.getNumberOfInjection());
        vaccineRepository.save(vaccine);
        request.setVaccineId(vaccine.getId());
        injectionResultRepository.save(request.toEntity());
        return true;
    }

    @Override
    @Transactional(rollbackFor = {InsufficientInjectionException.class, SQLException.class})
    public boolean update(InjectionResultRequest request, String id) {
        Optional<InjectionResult> injectionResultOptional = injectionResultRepository.findById(id);
        if (injectionResultOptional.isEmpty()) {
            throw new NotFoundException(Message.MSG_182, id);
        }

        //start check vaccine and customer
        Optional<Vaccine> vaccineOptional = vaccineRepository.findById(request.getVaccineId());
        if (vaccineOptional.isEmpty()) {
            throw new NotFoundException(Message.MSG_153, request.getVaccineId());
        }

        Optional<Customer> customerOptional = customerRepository.findById(request.getCustomerId());
        if (customerOptional.isEmpty()) {
            throw new NotFoundException(Message.MSG_111, request.getCustomerId());
        }

        InjectionResult injectionResult = injectionResultOptional.get();
        Vaccine vaccine = vaccineOptional.get();
        Customer customer = customerOptional.get();

        if (!Status.ACTIVE.equals(customer.getStatus())) {
            throw new InvalidStatusException(Message.MSG_116);
        }
        if (vaccine.getNumberOfInjection() + injectionResult.getNumberOfInjection() < request.getNumberOfInjection()) {
            throw new InsufficientInjectionException(Message.MSG_181);
        }
        //end
        vaccine.setNumberOfInjection(vaccine.getNumberOfInjection() + injectionResult.getNumberOfInjection() - request.getNumberOfInjection());
        vaccineRepository.save(vaccine);

        injectionResult = request.toEntity();
        injectionResult.setId(id);
        injectionResultRepository.save(injectionResult);
        return true;
    }

    @Override
    public PageDto<InjectionResultDto> getAll(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<InjectionResult> injectionResultPage = injectionResultRepository
                .findByCustomerFullNameContainsIgnoreCaseOrVaccineNameContainsIgnoreCase(search, search, pageable);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        List<InjectionResultDto> injectionResultDtoList = injectionResultPage.get()
                .map(jr -> {
                    InjectionResultDto dto = mapper.map(jr, InjectionResultDto.class);
                    dto.setVaccineName(jr.getVaccine().getName());
                    dto.setPrevention(jr.getVaccine().getVaccineType().getName());
                    dto.setPrevention(jr.getPrevention());
                    dto.setCustomer(String.format("%s - %s - %s", jr.getCustomer().getIdentityCard(), jr.getCustomer().getFullName(), jr.getCustomer().getDateOfBirth().format(dtf)));
                    return dto;
                }).toList();
        return new PageDto<>(
                page,
                size,
                injectionResultPage.getTotalPages(),
                injectionResultPage.getTotalElements(),
                injectionResultDtoList
        );
    }

    @Override
    public InjectionResultDetailDto getById(String id) {
        if (!injectionResultRepository.existsById(id)) {
            throw new NotFoundException(Message.MSG_182, id);
        }
        var injectionResult = injectionResultRepository.findById(id).get();
        var dto = mapper.map(injectionResult, InjectionResultDetailDto.class);
        dto.setCustomerId(injectionResult.getCustomer().getId());
        dto.setVaccineId(injectionResult.getVaccine().getId());
        return dto;
    }

    @Override
    @Transactional(rollbackFor = SQLException.class)
    public void deleteByIds(String ids) {
        String[] idList = ids.split(",");
        List<InjectionResult> injectionResults = injectionResultRepository.findAllById(Arrays.asList(idList));
        for (InjectionResult injectionResult : injectionResults) {
            injectionResult.setDeleted(true);
            Vaccine vaccine = vaccineRepository.findById(injectionResult.getId()).orElse(null);
            if (vaccine != null) {
                vaccine.setNumberOfInjection(vaccine.getNumberOfInjection() + injectionResult.getNumberOfInjection());
                vaccineRepository.save(vaccine);
            }
        }
        injectionResultRepository.saveAll(injectionResults);
    }


}
