package fa.training.backend.service.impl;

import fa.training.backend.dto.request.vaccine.VaccineAddRequest;
import fa.training.backend.dto.request.vaccine.VaccineUpdateRequest;
import fa.training.backend.dto.request.vaccine.VaccineInjectionScheduleRequestDto;
import fa.training.backend.dto.response.PageDto;
import fa.training.backend.dto.response.vaccine.VaccineIndividualDto;
import fa.training.backend.dto.response.vaccine.VaccineUpdateDto;
import fa.training.backend.exception.common.InvalidFileException;
import fa.training.backend.exception.common.InvalidListException;
import fa.training.backend.exception.common.NotFoundException;
import fa.training.backend.exception.vaccine.InvalidVaccineImportException;
import fa.training.backend.model.Vaccine;
import fa.training.backend.model.VaccineType;
import fa.training.backend.repository.VaccineRepository;
import fa.training.backend.repository.VaccineTypeRepository;
import fa.training.backend.service.VaccineService;
import fa.training.backend.util.Message;
import fa.training.backend.util.Status;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.util.*;

@Service
public class VaccineServiceImpl implements VaccineService {
    private VaccineTypeRepository vaccineTypeRepository;
    private VaccineRepository vaccineRepository;
    private final String sheetName = "Vaccines";
    private ModelMapper modelMapper;

    @Autowired
    public VaccineServiceImpl(VaccineTypeRepository vaccineTypeRepository, VaccineRepository vaccineRepository, ModelMapper modelMapper) {
        this.vaccineTypeRepository = vaccineTypeRepository;
        this.vaccineRepository = vaccineRepository;
        this.modelMapper = modelMapper;
    }

    public boolean add(VaccineAddRequest vaccineAddRequest) {

        if (!vaccineTypeRepository.existsById(vaccineAddRequest.getVaccineTypeId())) {
            throw new NotFoundException(Message.MSG_128, vaccineAddRequest.getVaccineTypeId());
        }
        Vaccine vaccine = modelMapper.map(vaccineAddRequest, Vaccine.class);
        vaccine.setStatus(Status.ACTIVE);
        vaccine.setId(null);

        vaccineRepository.save(vaccine);
        return true;
    }

    public boolean update(VaccineUpdateRequest vaccineUpdateRequest, String id) {
        Optional<Vaccine> existingVaccine = vaccineRepository.findById(id);
        if (existingVaccine.isEmpty()) {
            throw new NotFoundException(Message.MSG_153, id);

        }
        if (!vaccineTypeRepository.existsById(vaccineUpdateRequest.getVaccineTypeId())) {
            throw new NotFoundException(Message.MSG_128, vaccineUpdateRequest.getVaccineTypeId());
        }
        Vaccine vaccine = modelMapper.map(vaccineUpdateRequest, Vaccine.class);
        vaccine.setId(id);

        vaccineRepository.save(vaccine);
        return true;
    }

    public PageDto<VaccineIndividualDto> getAll(Pageable pageable, String query) {
        var page = vaccineRepository.findByNameContainsIgnoreCase(pageable, query);
        PageDto<VaccineIndividualDto> pageDto = new PageDto<>();
        List<VaccineIndividualDto> vaccineDtos = page.stream().map(vaccine -> {
            var vaccineDto = modelMapper.map(vaccine, VaccineIndividualDto.class);
            vaccineDto.setVaccineTypeName(vaccine.getVaccineType().getName());
            return vaccineDto;
        }).toList();
        pageDto.setData(vaccineDtos);
        pageDto.setTotalPages(page.getTotalPages());
        pageDto.setPage(pageable.getPageNumber() + 1);
        pageDto.setSize(pageable.getPageSize());
        pageDto.setTotalElements(page.getTotalElements());
        return pageDto;
    }

    public VaccineUpdateDto getById(String id) {
        Optional<Vaccine> existingVaccine = vaccineRepository.findById(id);
        if (existingVaccine.isEmpty()) {
            throw new NotFoundException(Message.MSG_153, id);
        }
        return modelMapper.map(existingVaccine.get(), VaccineUpdateDto.class);
    }

    public VaccineUpdateDto getByName(String name) {
        Optional<Vaccine> existingVaccine = vaccineRepository.findByName(name);
        if (existingVaccine.isEmpty()) {
            throw new NotFoundException(Message.MSG_153, name);
        }
        return modelMapper.map(existingVaccine.get(), VaccineUpdateDto.class);
    }

    public boolean switchStatus(String id) {
        Optional<Vaccine> existingVaccine = vaccineRepository.findById(id);
        if (existingVaccine.isEmpty()) {
            throw new NotFoundException(Message.MSG_153, id);
        }
        Vaccine vaccine = existingVaccine.get();
        Status newStatus = Status.ACTIVE.equals(vaccine.getStatus()) ? Status.INACTIVE : Status.ACTIVE;
        vaccine.setStatus(newStatus);
        vaccineRepository.save(vaccine);
        return true;
    }

    @Override
    public List<VaccineInjectionScheduleRequestDto> getAllVaccines() {
        // get vaccine active
        List<Vaccine> vaccineList = vaccineRepository.findAllByStatus(Status.ACTIVE);
        return vaccineList.stream()
                .map(vaccine -> modelMapper.map(vaccine, VaccineInjectionScheduleRequestDto.class))
                .toList();
    }

    @Override
    public boolean updateVaccineInactive(String ids) {
        List<String> idList = Arrays.asList(ids.split(","));
        if (ids == null || ids.isEmpty()) {
            return false;
        }
        List<Vaccine> vaccines = vaccineRepository.findAllById(idList);

        if (vaccines.size() != idList.size()) {
            return false;
        }
        List<String> invalidIdList = new ArrayList<>();
        for (Vaccine vaccine : vaccines) {
            if (Status.ACTIVE.equals(vaccine.getStatus())) {
                vaccine.setStatus(Status.INACTIVE);
            } else {
                invalidIdList.add(vaccine.getId());
            }
        }

        if (!invalidIdList.isEmpty()) {
            throw new InvalidListException(Message.MSG_151, invalidIdList);
        }
        vaccineRepository.saveAll(vaccines);
        return true;

    }

    // Import file
    @Override
    public byte[] createTemplate() throws IOException {
        List<String> headers = List.of(

                "Vaccine name",
                "Vaccine type ID",
                "Number of injection",
                "Usage",
                "Indication",
                "Contraindication",
                "Origin"
        );
        final String excelFileName = "Vaccines.xlsx";
        Path path = null;
        try (Workbook workbook = new XSSFWorkbook();
             FileOutputStream outputStream = new FileOutputStream(excelFileName);) {
            Sheet sheet = workbook.createSheet(this.sheetName);

            //Font bold
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.BLACK.index);

            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setFont(headerFont);

            List<VaccineType> vaccineTypes = vaccineTypeRepository.findAll();

            //Vaccine types header
            Row row = sheet.createRow(0);
            Cell cell = row.createCell(0);
            cell.setCellValue("Vaccine type ID");
            cell.setCellStyle(cellStyle);

            cell = row.createCell(1);
            cell.setCellValue("Vaccine type name");
            cell.setCellStyle(cellStyle);

            String[] vaccineTypeIds = new String[vaccineTypes.size()];

            //vaccine types id, name
            for (int i = 0; i < vaccineTypes.size(); i++) {
                row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(vaccineTypes.get(i).getId());
                row.createCell(1).setCellValue(vaccineTypes.get(i).getName());

                vaccineTypeIds[i] = vaccineTypes.get(i).getId();
            }

            //Headers
            row = sheet.getRow(0);
            for (int i = 0; i < headers.size(); i++) {
                cell = row.createCell(i + 4);
                cell.setCellValue(headers.get(i));
                cell.setCellStyle(cellStyle);
            }

            //Validate vaccine types
//            DataValidationHelper validationHelper = sheet.getDataValidationHelper();
//            DataValidationConstraint constraint = validationHelper.createExplicitListConstraint(vaccineTypeIds);
//            CellRangeAddressList addressList = new CellRangeAddressList(1, 1048575, 5, 5); // Assign to cell F2 to F201
//            DataValidation dataValidation = validationHelper.createValidation(constraint, addressList);
//            dataValidation.createPromptBox("Selection Tip", "Please select a value from the drop-down list.");
//            dataValidation.createErrorBox("Invalid Input", "The value entered is not valid. Please select from the drop-down list.");
//            dataValidation.setSuppressDropDownArrow(true);
//            dataValidation.setShowErrorBox(true);
//            sheet.addValidationData(dataValidation);

            workbook.write(outputStream);

            path = Paths.get(excelFileName);
            byte[] data = Files.readAllBytes(path);

            return data;
        } catch (Exception e) {
            System.out.println(e);
            return null;
        } finally {
            Files.delete(path);
        }
    }

    @Override
    public void importVaccine(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);
        final String extension = "xlsx";

        if (!extension.equals(fileExtension)) {
            throw new InvalidFileException(Message.MSG_102);
        }
        StringBuilder errorData = new StringBuilder("Your file has the following errors:<br><br>");
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            DataFormatter dataFormatter = new DataFormatter();
            Sheet sheet = workbook.getSheet(this.sheetName);
            Iterator<Row> iterator = sheet.iterator();
            //Skip header row
            if (iterator.hasNext()) {
                iterator.next();
            }

            //Read data from E-column to M-column ~ column 3-12
            List<Vaccine> vaccines = new ArrayList<>();
            boolean isValid = true;

            while (iterator.hasNext()) {
                Row row = iterator.next();

                if (row.getLastCellNum() < 4 && (row.getCell(4) == null || dataFormatter.formatCellValue(row.getCell(4)).isEmpty())) {
                    break;
                }
                Vaccine vaccine = Vaccine.builder()
                        .id(null)
                        .name(dataFormatter.formatCellValue(row.getCell(4)))
                        .vaccineType(new VaccineType(dataFormatter.formatCellValue(row.getCell(5))))
                        .numberOfInjection(Integer.parseInt(dataFormatter.formatCellValue(row.getCell(6))))
                        .usage(dataFormatter.formatCellValue(row.getCell(7)))
                        .indication(dataFormatter.formatCellValue(row.getCell(8)))
                        .contraindication(dataFormatter.formatCellValue(row.getCell(9)))
                        .origin(dataFormatter.formatCellValue(row.getCell(10)))
                        .status(Status.ACTIVE)
                        .build();
                StringBuilder validateResult = validateVaccine(vaccine, row.getRowNum() + 1);
                if (validateResult != null) {
                    isValid = false;
                    errorData.append(validateResult);
                }

                vaccines.add(vaccine);


            }
            if (isValid) {
                vaccineRepository.saveAll(vaccines);
            } else {
                throw new InvalidVaccineImportException(Message.MSG_154, errorData.toString());
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            throw new InvalidVaccineImportException(Message.MSG_154, errorData.toString());
        }
    }

    @Override
    public void deleteByIds(String ids) {
        String[] idList = ids.split(",");
        List<Vaccine> vaccines = vaccineRepository.findAllById(Arrays.asList(idList));
        for (Vaccine vaccine : vaccines) {
            vaccine.setDeleted(true);
        }
        vaccineRepository.saveAll(vaccines);
    }

    private StringBuilder validateVaccine(Vaccine vaccine, int row) {
        StringBuilder errors = new StringBuilder();
        errors.append("Row ");
        errors.append(row);
        errors.append(": ");
        boolean isValid = true;
        if (vaccine.getVaccineType() == null || vaccine.getVaccineType().getId() == null
                || vaccine.getVaccineType().getId().isBlank()) {
            isValid = false;
            errors.append("Vaccine type is required. ");
        }
        VaccineType vaccineType = vaccineTypeRepository.findById(vaccine.getVaccineType().getId()).orElse(null);
        if (vaccineType == null) {
            isValid = false;
            errors.append("Vaccine type does not exist. ");
        }

        if (vaccineType != null && vaccineType.getStatus().equals(Status.INACTIVE)) {
            isValid = false;
            errors.append("Vaccine type's status is invalid. ");
        }

        if (vaccine.getNumberOfInjection() <= 0) {
            isValid = false;
            errors.append("Number of injection must be a positive integer. ");
        }

        if (vaccine.getName() == null || vaccine.getName().isBlank()) {
            isValid = false;
            errors.append("Vaccine name is required. ");
        }



        if (isValid) {
            return null;
        }
        errors.append("<br>");
        return errors;

    }
}
