package fa.training.backend.controller;

import fa.training.backend.dto.request.vaccine.VaccineAddRequest;
import fa.training.backend.dto.request.vaccine.VaccineUpdateRequest;
import fa.training.backend.dto.request.vaccine.VaccineInjectionScheduleRequestDto;
import fa.training.backend.dto.response.*;
import fa.training.backend.dto.response.vaccine.VaccineIndividualDto;
import fa.training.backend.dto.response.vaccine.VaccineUpdateDto;
import fa.training.backend.service.VaccineService;
import fa.training.backend.util.Message;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/vaccines")
public class VaccineController {
    private final VaccineService vaccineService;

    @Autowired
    public VaccineController(VaccineService vaccineService) {
        this.vaccineService = vaccineService;
    }

    @GetMapping
    public ResponseEntity<Response<PageDto<VaccineIndividualDto>>> getAllVaccines(
            @RequestParam(defaultValue = "1", required = false) int page,
            @RequestParam(defaultValue = "10", required = false) int size,
            @RequestParam(defaultValue = "", required = false) String query
    ) {
        Pageable pageable = PageRequest.of(page - 1, size);
        PageDto<VaccineIndividualDto> vaccines = vaccineService.getAll(pageable, query);
        return ResponseEntity.ok(new Response<>(
                        Message.MSG_251.getCode(),
                        Message.MSG_251.getMessage(),
                        vaccines));
    }

    @GetMapping("/names")
    public ResponseEntity<Response<List<VaccineInjectionScheduleRequestDto>>> getListAllVaccine() {
        List<VaccineInjectionScheduleRequestDto> vaccineNameList = vaccineService.getAllVaccines();
        return ResponseEntity.ok(new Response<>(
                        Message.MSG_256.getCode(),
                        Message.MSG_256.getMessage(),
                        vaccineNameList));

    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> getVaccineById(@PathVariable String id) {
        VaccineUpdateDto vaccine = vaccineService.getById(id);
        return ResponseEntity.ok(Response.builder()
                        .code(Message.MSG_253.getCode())
                        .description(Message.MSG_253.getMessage())
                        .data(vaccine)
                        .build());
    }

    @PostMapping
    public ResponseEntity<Response> addVaccine(@Valid @RequestBody VaccineAddRequest vaccineAddRequest) {
        boolean result = vaccineService.add(vaccineAddRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Response.builder()
                        .code(Message.MSG_252.getCode())
                        .description(Message.MSG_252.getMessage())
                        .data(result)
                        .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Response> updateVaccine(@Valid @RequestBody VaccineUpdateRequest vaccineUpdateRequest, @PathVariable String id) {
        boolean updated = vaccineService.update(vaccineUpdateRequest, id);
        return ResponseEntity.ok(Response.builder()
                        .code(Message.MSG_254.getCode())
                        .description(Message.MSG_254.getMessage())
                        .data(updated)
                        .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response> switchStatus(@PathVariable String id) {
        boolean deleted = vaccineService.switchStatus(id);
        return ResponseEntity.ok(Response.builder()
                        .code(Message.MSG_255.getCode())
                        .description(Message.MSG_255.getMessage())
                        .data(deleted)
                        .build());
    }


    @PutMapping("/in-active")
    public ResponseEntity<Response> updateVaccineInactive(@RequestBody String ids) {

        boolean result = vaccineService.updateVaccineInactive(ids);

        if (!result) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Response.builder()
                            .code(Message.MSG_152.getCode())
                            .description(Message.MSG_152.getMessage())
                            .build());
        }
        return ResponseEntity.ok(Response.builder()
                        .code(Message.MSG_257.getCode())
                        .description(Message.MSG_257.getMessage())
                        .data(true)
                        .build());
    }


    // Upload file
    @GetMapping("/template")
    public ResponseEntity createTemplate() throws IOException {
        byte[] template = vaccineService.createTemplate();
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(String.valueOf(com.google.common.net.MediaType.MICROSOFT_EXCEL)))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; fileName=\"Vaccines.xlsx" + "\"")
                .body(template);
    }

    @PostMapping("/template")
    public ResponseEntity<Response> importVaccines(MultipartFile file) {
        vaccineService.importVaccine(file);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Response.builder()
                        .code(Message.MSG_252.getCode())
                        .description(Message.MSG_252.getMessage())
                        .build());
    }
}
