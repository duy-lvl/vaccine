package fa.training.backend.controller;

import fa.training.backend.dto.response.Response;
import fa.training.backend.service.FirebaseService;
import fa.training.backend.util.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;

@RestController
@RequestMapping("/api/v1/files")
public class FileController {
    private final FirebaseService firebaseService;

    @Autowired
    public FileController(FirebaseService firebaseService) {
        this.firebaseService = firebaseService;
    }

    @PostMapping
    public ResponseEntity<Response> uploadFile(MultipartFile file) {
        try {
            String fileUrl = firebaseService.upload(file);
            return ResponseEntity.ok().body(
                    new Response<>(
                            Message.MSG_204.getCode(),
                            Message.MSG_204.getMessage(),
                            fileUrl
                    )
            );
        } catch (Exception e) {
            return ResponseEntity.ok().body(
                    new Response(
                            Message.MSG_301.getCode(),
                            Message.MSG_301.getMessage(),
                            null
                    )

            );
        }

    }

    @GetMapping("/{fileName}")
    public ResponseEntity<Response<URL>> getFileUrl(@PathVariable String fileName) {
        return ResponseEntity.ok()
                .body(new Response<>(
                        Message.MSG_202.getCode(),
                        Message.MSG_202.getMessage(),
                        firebaseService.getFileUrl(fileName)
                ));
    }

    @DeleteMapping("/{fileName}")
    public ResponseEntity<Response<Boolean>> deleteFile(@PathVariable String fileName) {
        return ResponseEntity.ok()
                .body(new Response<>(
                        Message.MSG_203.getCode(),
                        Message.MSG_203.getMessage(),
                        firebaseService.deleteFile(fileName)
                ));
    }
}
