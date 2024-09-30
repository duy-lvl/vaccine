package fa.training.backend.controller;

import fa.training.backend.dto.request.injection_schedule.InjectionScheduleAddRequest;
import fa.training.backend.dto.request.injection_schedule.InjectionScheduleUpdateRequest;
import fa.training.backend.dto.response.injection_schedule.InjectionScheduleDetailDto;
import fa.training.backend.dto.response.injection_schedule.InjectionScheduleDto;
import fa.training.backend.dto.response.PageDto;
import fa.training.backend.dto.response.Response;
import fa.training.backend.service.InjectionScheduleService;
import fa.training.backend.util.Message;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/injection-schedules")
public class InjectionScheduleController {

    private final InjectionScheduleService injectionScheduleService;

    @Autowired
    public InjectionScheduleController(InjectionScheduleService injectionScheduleService) {
        this.injectionScheduleService = injectionScheduleService;
    }

    @GetMapping
    public ResponseEntity<Response<PageDto<InjectionScheduleDto>>> getAll(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "") String search
    ) {
        return ResponseEntity.ok(
                new Response<>(
                        Message.MSG_271.getCode(),
                        Message.MSG_271.getMessage(),
                        injectionScheduleService.get(page, size, search)
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<InjectionScheduleDetailDto>> getById(@PathVariable String id) {
        return ResponseEntity.ok(
                new Response<>(
                        Message.MSG_272.getCode(),
                        Message.MSG_272.getMessage(),
                        injectionScheduleService.getById(id)
                )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<Response<Boolean>> update(
            @PathVariable String id,
            @Valid @RequestBody InjectionScheduleUpdateRequest request) {
        return ResponseEntity.ok(
                new Response<>(
                        Message.MSG_274.getCode(),
                        Message.MSG_274.getMessage(),
                        injectionScheduleService.update(request, id)
                )
        );
    }

    @PostMapping
    public ResponseEntity<Response<Boolean>> add(@Valid @RequestBody InjectionScheduleAddRequest request
    ) {

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new Response<>(
                        Message.MSG_273.getCode(),
                        Message.MSG_273.getMessage(),
                        injectionScheduleService.add(request)
                )
        );
    }
}
