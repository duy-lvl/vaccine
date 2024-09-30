package fa.training.backend.controller;

import fa.training.backend.dto.request.injection_result.InjectionResultRequest;
import fa.training.backend.dto.request.injection_result.InjectionResultAddRequest;
import fa.training.backend.dto.response.*;
import fa.training.backend.dto.response.injection_result.InjectionResultDetailDto;
import fa.training.backend.dto.response.injection_result.InjectionResultDto;
import fa.training.backend.dto.response.injection_schedule.InjectionScheduleDto;
import fa.training.backend.service.InjectionResultService;
import fa.training.backend.service.InjectionScheduleService;
import fa.training.backend.util.Message;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/injection-results")
public class InjectionResultController {
    private final InjectionResultService injectionResultService;
    @Autowired
    private InjectionScheduleService injectionScheduleService;

    @Autowired
    public InjectionResultController(InjectionResultService injectionResultService) {
        this.injectionResultService = injectionResultService;
    }

    @GetMapping
    public ResponseEntity<Response<PageDto<InjectionResultDto>>> getAll(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "") String search
    ) {
        return ResponseEntity.ok(
                new Response<>(
                        Message.MSG_281.getCode(),
                        Message.MSG_281.getMessage(),
                        injectionResultService.getAll(page, size, search)
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<InjectionResultDetailDto>> getById(@PathVariable String id) {
        return ResponseEntity.ok(
                new Response<>(
                        Message.MSG_282.getCode(),
                        Message.MSG_282.getMessage(),
                        injectionResultService.getById(id)
                )
        );
    }

    @GetMapping("/schedules")
    public ResponseEntity<Response<PageDto<InjectionScheduleDto>>> getAllByRequest(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @Valid InjectionResultAddRequest request) {
        return ResponseEntity.ok(
                new Response<>(
                        Message.MSG_271.getCode(),
                        Message.MSG_271.getMessage(),
                        injectionScheduleService.getInjectionSchedules(page, size, request)
                )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<Response<Boolean>> update(
            @PathVariable String id,
            @Valid @RequestBody InjectionResultRequest request) {
        return ResponseEntity.ok(
                new Response<>(
                        Message.MSG_284.getCode(),
                        Message.MSG_284.getMessage(),
                        injectionResultService.update(request, id)
                )
        );
    }

    @PostMapping
    public ResponseEntity<Response<Boolean>> add(@Valid @RequestBody InjectionResultRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new Response<>(
                        Message.MSG_283.getCode(),
                        Message.MSG_283.getMessage(),
                        injectionResultService.add(request)
                )
        );
    }

    @DeleteMapping("/{ids}")
    public ResponseEntity<Response<Boolean>> deleteEmployeesByIds(@PathVariable String ids) {
        injectionResultService.deleteByIds(ids);

        Response<Boolean> response = new Response<>(
                Message.MSG_285.getCode(),
                Message.MSG_285.getMessage(),
                true
        );
        return ResponseEntity.ok(response);
    }
}
