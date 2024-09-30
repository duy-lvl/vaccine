package fa.training.backend.controller;

import fa.training.backend.dto.request.vaccine_type.NewVaccineTypeRequest;
import fa.training.backend.dto.request.vaccine_type.UpdateVaccineTypeRequest;
import fa.training.backend.dto.response.PageDto;
import fa.training.backend.dto.response.Response;
import fa.training.backend.dto.response.vaccine_type.VaccineTypeListDto;
import fa.training.backend.dto.response.vaccine_type.VaccineTypeUpdateDto;
import fa.training.backend.service.VaccineTypeService;
import fa.training.backend.util.Message;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/vaccine-types")
public class VaccineTypeController {
    private final VaccineTypeService vaccineTypeService;

    @Autowired
    public VaccineTypeController(VaccineTypeService vaccineTypeService) {
        this.vaccineTypeService = vaccineTypeService;
    }

    @PostMapping
    public ResponseEntity<Response> add(
            @Valid @RequestBody NewVaccineTypeRequest newVaccineTypeRequest
    ) {

        String id = vaccineTypeService.add(newVaccineTypeRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(Response.builder()
                .code(Message.MSG_221.getCode())
                .description(Message.MSG_221.getMessage())
                .data(id)
                .build());
    }

    @PutMapping("/in-active")
    public ResponseEntity<Response> updateVaccineTypeInactive(@RequestBody String ids) {

        List<String> idList = Arrays.asList(ids.split(","));
        if (ids == null || ids.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Response.builder()
                            .code(Message.MSG_122.getCode())
                            .description(Message.MSG_122.getMessage())
                            .build());
        }

        boolean result = vaccineTypeService.updateVaccineTypeInactive(idList);

        if (!result) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Response.builder()
                            .code(Message.MSG_126.getCode())
                            .description(Message.MSG_126.getMessage())
                            .build());
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(Response.builder()
                        .code(Message.MSG_224.getCode())
                        .description(Message.MSG_224.getMessage())
                        .data(true)
                        .build());
    }

    @PutMapping("/active")
    public ResponseEntity<Response> updateVaccineTypeActive(@RequestBody String ids) {
        List<String> idList = Arrays.asList(ids.split(","));

        if (ids == null || ids.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Response.builder()
                            .code(Message.MSG_122.getCode())
                            .description(Message.MSG_122.getMessage())
                            .build());
        }

        boolean result = vaccineTypeService.updateVaccineTypeActive(idList);

        if (!result) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Response.builder()
                            .code(Message.MSG_126.getCode())
                            .description(Message.MSG_126.getMessage())
                            .build());
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(Response.builder()
                        .code(Message.MSG_224.getCode())
                        .description(Message.MSG_224.getMessage())
                        .data(true)
                        .build());
    }


    @GetMapping("/{id}")
    public ResponseEntity<Response> getVaccineTypeById(@PathVariable String id) {
        VaccineTypeUpdateDto vaccineTypeUpdateDto = vaccineTypeService.findById(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Response.builder()
                        .code(Message.MSG_212.getCode())
                        .description(Message.MSG_212.getMessage())
                        .data(vaccineTypeUpdateDto)
                        .build());
    }


    @PutMapping("/{id}")
    public ResponseEntity<Response> updateVaccineType(@RequestBody UpdateVaccineTypeRequest updateVaccineTypeRequest, @PathVariable String id) {

        boolean updated = vaccineTypeService.update(updateVaccineTypeRequest, id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Response.builder()
                        .code(Message.MSG_224.getCode())
                        .description(Message.MSG_224.getMessage())
                        .data(updated)
                        .build());

    }


    @GetMapping
    public ResponseEntity<Response<PageDto<VaccineTypeListDto>>> searchVaccineTypes(
            @RequestParam(defaultValue = "1", required = false) int page,
            @RequestParam(defaultValue = "5", required = false) int size,
            @RequestParam(defaultValue = "", required = false) String query) {

        Pageable pageable = PageRequest.of(page - 1, size);
        PageDto<VaccineTypeListDto> pageDto = vaccineTypeService.searchVaccineTypes(query, pageable);

        if (pageDto.getData().isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new Response<>(Message.MSG_129.getCode(),
                            Message.MSG_129.getMessage(), null));
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(new Response<>(Message.MSG_222.getCode(),
                        Message.MSG_222.getMessage(), pageDto));

    }

    @GetMapping("/names")
    public ResponseEntity<Response> getNames(@RequestParam(required = false, defaultValue = "true") boolean isActive) {
        return ResponseEntity.ok(
                new Response<>(
                        Message.MSG_222.getCode(),
                        Message.MSG_222.getMessage(),
                        vaccineTypeService.getNames(isActive)
                )
        );

    }

    @PutMapping("/{id}/image")
    public ResponseEntity<Response<Boolean>> updateImage(@RequestBody String imageUrl, @PathVariable String id) {
        boolean isUpdated = vaccineTypeService.updateImage(id, imageUrl);
        Response<Boolean> response;
        if (isUpdated) {
            response = new Response<>(
                    Message.MSG_224.getCode(),
                    Message.MSG_224.getMessage(),
                    true
            );
            return ResponseEntity.ok(response);
        } else {
            response = new Response<>(
                    Message.MSG_127.getCode(),
                    Message.MSG_127.getMessage(),
                    false
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

    }
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Response> deleteVaccineType(@PathVariable String id) {
//        boolean deleted = vaccineTypeService.deleteById(id);
//        return ResponseEntity.status(HttpStatus.OK)
//                .body(Response.builder()
//                        .code(Message.MSG_217.getCode())
//                        .description(Message.MSG_217.getMessage())
//                        .data(deleted)
//                        .build());
//    }
}