package fa.training.backend.controller;

import fa.training.backend.dto.request.news.NewsRequest;
import fa.training.backend.dto.response.Response;
import fa.training.backend.service.NewsService;
import fa.training.backend.util.Message;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/news")
public class NewsController {
    private final NewsService newsService;

    @Autowired
    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping
    public ResponseEntity<Response> getAll(
            @RequestParam(defaultValue = "1", required = false) int page,
            @RequestParam(defaultValue = "10", required = false) int size,
            @RequestParam(defaultValue = "", required = false) String search
    ) {
        return ResponseEntity.ok(
                Response.builder()
                        .code(Message.MSG_241.getCode())
                        .description(Message.MSG_241.getMessage())
                        .data(newsService.getAll(page, size, search))
                        .build()
        );
    }

    @PostMapping
    public ResponseEntity<Response> create(@Valid @RequestBody NewsRequest newsRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                Response.builder()
                        .code(Message.MSG_243.getCode())
                        .description(Message.MSG_243.getMessage())
                        .data(newsService.create(newsRequest))
                        .build()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<Response> update(@PathVariable String id, @Valid @RequestBody NewsRequest newsRequest) {
        newsService.update(newsRequest, id);

        return ResponseEntity.ok(
                Response.builder()
                        .code(Message.MSG_244.getCode())
                        .description(Message.MSG_244.getMessage())
                        .data(true).build()
        );

    }

    @DeleteMapping("/{ids}")
    public ResponseEntity<Response> delete(@PathVariable String ids) {
        newsService.deleteByIds(ids);
        return ResponseEntity.status(HttpStatus.OK).body(
                Response.builder()
                        .code(Message.MSG_245.getCode())
                        .description(Message.MSG_245.getMessage())
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> getById(@PathVariable String id) {
        newsService.getById(id);

        return ResponseEntity.status(HttpStatus.OK).body(
                Response.builder().code(Message.MSG_211.getCode())
                        .description(Message.MSG_211.getMessage())
                        .data(newsService.getById(id)).build());


    }
}
