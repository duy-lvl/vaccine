package fa.training.backend.controller;

import fa.training.backend.dto.request.news_type.NewsTypeRequest;
import fa.training.backend.dto.response.Response;
import fa.training.backend.service.NewsTypeService;
import fa.training.backend.util.Message;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/news-types")
public class NewsTypeController {
    private final NewsTypeService newsTypeService;

    @Autowired
    public NewsTypeController(NewsTypeService newsTypeService) {
        this.newsTypeService = newsTypeService;
    }

    @GetMapping
    public ResponseEntity<Response> all() {
        return ResponseEntity.ok(Response.builder()
                .code(Message.MSG_231.getCode())
                .description(Message.MSG_231.getMessage())
                .data(newsTypeService.getAll()).build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> getById(@PathVariable String id) {
        return ResponseEntity.ok(Response.builder()
                .code(Message.MSG_232.getCode())
                .description(Message.MSG_232.getMessage())
                .data(newsTypeService.getById(id)).build());
    }

    @PostMapping
    public ResponseEntity<Response> create(@Valid @RequestBody NewsTypeRequest newsType) {
        boolean result = newsTypeService.create(newsType);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        Response.builder()
                                .code(Message.MSG_233.getCode())
                                .description(Message.MSG_233.getMessage())
                                .data(result)
                                .build()
                );
    }

    @PutMapping("/{id}")
    public ResponseEntity<Response> update(@Valid @RequestBody NewsTypeRequest newsType, @PathVariable String id) {
        boolean isUpdate = newsTypeService.update(newsType, id);

        return ResponseEntity.ok(
                Response.builder()
                        .code(Message.MSG_234.getCode())
                        .description(Message.MSG_234.getMessage())
                        .data(isUpdate).build()
        );
    }

    @DeleteMapping
    public ResponseEntity<Response<Boolean>> deleteNewsTypesByIds(@RequestParam String ids) {
        boolean isDeleted = newsTypeService.deleteNewsTypeByIds(ids);
        Response<Boolean> response = new Response<>(
                Message.MSG_235.getCode(),
                Message.MSG_235.getMessage(),
                isDeleted
        );

        return ResponseEntity.ok(response);
    }
}
