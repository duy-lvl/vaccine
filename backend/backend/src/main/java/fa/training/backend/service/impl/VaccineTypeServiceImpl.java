package fa.training.backend.service.impl;

import fa.training.backend.dto.request.vaccine_type.NewVaccineTypeRequest;
import fa.training.backend.dto.request.vaccine_type.UpdateVaccineTypeRequest;
import fa.training.backend.dto.response.*;
import fa.training.backend.dto.response.vaccine.VaccineNameDto;
import fa.training.backend.dto.response.vaccine_type.VaccineTypeListDto;
import fa.training.backend.dto.response.vaccine_type.VaccineTypeNameDto;
import fa.training.backend.dto.response.vaccine_type.VaccineTypeUpdateDto;
import fa.training.backend.exception.common.*;
import fa.training.backend.exception.vaccine_type.CodeDuplicateException;
import fa.training.backend.exception.vaccine_type.NameDuplicateException;
import fa.training.backend.model.Vaccine;
import fa.training.backend.model.VaccineType;
import fa.training.backend.repository.VaccineRepository;
import fa.training.backend.repository.VaccineTypeRepository;
import fa.training.backend.service.FirebaseService;
import fa.training.backend.service.VaccineTypeService;
import fa.training.backend.util.Message;
import fa.training.backend.util.Status;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class VaccineTypeServiceImpl implements VaccineTypeService {

    private final VaccineTypeRepository vaccineTypeRepository;

    private final FirebaseService firebaseService;
    private final VaccineRepository vaccineRepository;

    private ModelMapper modelMapper;

    @Autowired
    public VaccineTypeServiceImpl(VaccineTypeRepository vaccineTypeRepository, FirebaseService firebaseService, VaccineRepository vaccineRepository, ModelMapper modelMapper) {
        this.vaccineTypeRepository = vaccineTypeRepository;
        this.firebaseService = firebaseService;
        this.vaccineRepository = vaccineRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public PageDto<VaccineTypeListDto> getAll(Pageable pageable) {
        var page = vaccineTypeRepository.findAll(pageable);
        PageDto<VaccineTypeListDto> pageDto = new PageDto<>();
        pageDto.setTotalPages(page.getTotalPages());
        pageDto.setPage(pageable.getPageNumber());
        pageDto.setSize(pageable.getPageSize());
        List<VaccineTypeListDto> vaccines = page.stream().map(
                vaccine -> modelMapper.map(vaccine, VaccineTypeListDto.class)).toList();

        pageDto.setData(vaccines);
        return pageDto;
    }

    @Override
    public VaccineTypeUpdateDto findById(String id) {
        Optional<VaccineType> vaccineType = vaccineTypeRepository.findById(id);
        if (vaccineType.isEmpty()) {
            throw new NotFoundException(Message.MSG_128, id);
        }
        var vaccineTypeUpdateDto = modelMapper.map(vaccineType.get(), VaccineTypeUpdateDto.class);
        try {
            vaccineTypeUpdateDto.setImageUrl(firebaseService.getFileUrl(vaccineType.get().getImageUrl()));
        } catch (Exception e) {
            vaccineTypeUpdateDto.setImageUrl(null);
        }
        vaccineTypeUpdateDto.setImageName(vaccineType.get().getImageUrl());
        return vaccineTypeUpdateDto;
    }

    @Override
    public void deleteByIds(String id) {
        String[] idList = id.split(",");
        List<VaccineType> vaccineTypes = vaccineTypeRepository.findAllById(Arrays.asList(idList));
        for (VaccineType vaccineType : vaccineTypes) {
            vaccineType.setDeleted(true);
        }
        vaccineTypeRepository.saveAll(vaccineTypes);
    }

    @Override
    public List<VaccineTypeNameDto> getNames(boolean isActive) {
        List<VaccineType> types;
        if (isActive) {
            types = vaccineTypeRepository.findByStatus(Status.ACTIVE);
        } else {
            types = vaccineTypeRepository.findAll();
        }
        List<VaccineTypeNameDto> result = new ArrayList<>();
        for (var type : types) {
            VaccineTypeNameDto dto = new VaccineTypeNameDto();
            dto.setId(type.getId());
            dto.setName(type.getName());
            List<Vaccine> vaccines;
            if (isActive) {
                vaccines = vaccineRepository.findByVaccineTypeIdAndStatus(type.getId(), Status.ACTIVE);
            } else {
                vaccines = vaccineRepository.findByVaccineTypeId(type.getId());
            }
            if (vaccines.isEmpty()) {
                continue;
            }
            dto.setVaccines(modelMapper.map(vaccines, new TypeToken<List<VaccineNameDto>>() {
            }.getType()));
            result.add(dto);
        }
        return result;
    }

    @Override
    public boolean updateImage(String id, String imageUrl) {
        Optional<VaccineType> vaccineTypeOptional = vaccineTypeRepository.findById(id);
        if (vaccineTypeOptional.isEmpty()) {
            return false;
        }
        VaccineType vaccineType = vaccineTypeOptional.get();
        vaccineType.setImageUrl(imageUrl);
        vaccineTypeRepository.save(vaccineType);
        return true;
    }


    @Override

    public String add(NewVaccineTypeRequest newVaccineTypeRequest) {
        boolean isCodeExisted = vaccineTypeRepository.existsByCode(newVaccineTypeRequest.getCode());
        if (isCodeExisted) {
            throw new CodeDuplicateException(Message.MSG_123);
        }
        boolean isNameExisted = vaccineTypeRepository.existsByName(newVaccineTypeRequest.getName());
        if (isNameExisted) {
            throw new NameDuplicateException(Message.MSG_124);
        }


        VaccineType vaccineType = modelMapper.map(newVaccineTypeRequest, VaccineType.class);
        vaccineType.setStatus(Status.ACTIVE);
        return vaccineTypeRepository.save(vaccineType).getId();

    }

    @Override
    public boolean update(UpdateVaccineTypeRequest updateVaccineTypeRequest, String id) {
        Optional<VaccineType> existingVaccineType = vaccineTypeRepository.findById(id);

        if (existingVaccineType.isEmpty()) {
            throw new NotFoundException(Message.MSG_128, id);
        }
        if (!existingVaccineType.get().getName().equals(updateVaccineTypeRequest.getName()) &&
                vaccineTypeRepository.existsByName(updateVaccineTypeRequest.getName())) {
            throw new NameDuplicateException(Message.MSG_121);
        }
        VaccineType vaccineType = modelMapper.map(updateVaccineTypeRequest, VaccineType.class);
        vaccineType.setId(id);
        vaccineTypeRepository.save(vaccineType);
        return true;
    }

    @Override
    public boolean updateVaccineTypeActive(List<String> ids) {
        List<VaccineType> vaccineTypes = vaccineTypeRepository.findAllById(ids);

        if (vaccineTypes.size() != ids.size()) {
            return false;
        }

        for (VaccineType vaccineType : vaccineTypes) {
            if (Status.INACTIVE.equals(vaccineType.getStatus())) {
                vaccineType.setStatus(Status.ACTIVE);
            }
        }

        vaccineTypeRepository.saveAll(vaccineTypes);
        return true;
    }


//
//    @Override
//    public boolean updateVaccineTypeInactive(String ids) {
//        List<String> idList = Arrays.asList(ids.split(","));
//        List<VaccineType> vaccineTypes = vaccineTypeRepository.findAllById(idList);
//
//
//        if (vaccineTypes.size() != idList.size()) {
//            return false;
//        }
//
//        for (VaccineType vaccineType : vaccineTypes) {
//            if (Status.ACTIVE.equals(vaccineType.getStatus())) {
//                vaccineType.setStatus(Status.INACTIVE);
//            } else {
//                return false;
//            }
//        }
//

    @Override
    public boolean updateVaccineTypeInactive(List<String> ids) {
        List<VaccineType> vaccineTypes = vaccineTypeRepository.findAllById(ids);

        if (vaccineTypes.size() != ids.size()) {
            return false;
        }
        List<String> invalidIdList = new ArrayList<>();
        for (VaccineType vaccineType : vaccineTypes) {
            if (Status.ACTIVE.equals(vaccineType.getStatus())) {
                vaccineType.setStatus(Status.INACTIVE);
                List<Vaccine> vaccines = vaccineRepository.findByVaccineTypeId(vaccineType.getId());
                for (Vaccine vaccine : vaccines) {
                    vaccine.setStatus(Status.INACTIVE);
                }
                vaccineRepository.saveAll(vaccines);
            } else {
                invalidIdList.add(vaccineType.getId());
            }
        }
        if (!invalidIdList.isEmpty()) {
            throw new InvalidListException(Message.MSG_126, invalidIdList);
        }
        vaccineTypeRepository.saveAll(vaccineTypes);
        return true;
    }


    @Override
    public PageDto<VaccineTypeListDto> searchVaccineTypes(String query, Pageable pageable) {
        Page<VaccineType> page;
        if (query == null || query.isEmpty()) {
            page = vaccineTypeRepository.findAll(pageable);
        } else {
            page = vaccineTypeRepository.findByCodeContainingOrNameContainingOrDescriptionContaining(query, query, query, pageable);
        }

        PageDto<VaccineTypeListDto> pageDto = new PageDto<>();
        pageDto.setTotalPages(page.getTotalPages());
        pageDto.setPage(pageable.getPageNumber() + 1);
        pageDto.setSize(pageable.getPageSize());

        List<VaccineTypeListDto> vaccines = page.stream()
                .map(vaccine -> modelMapper.map(vaccine, VaccineTypeListDto.class))
                .toList();
        pageDto.setData(vaccines);
        pageDto.setTotalElements(page.getTotalElements());
        return pageDto;
    }
}
