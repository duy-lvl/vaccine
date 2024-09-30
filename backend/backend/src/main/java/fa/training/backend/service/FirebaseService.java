package fa.training.backend.service;

import fa.training.backend.exception.common.InvalidFileException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;

public interface FirebaseService {
    String upload(MultipartFile file) throws IOException;
    URL getFileUrl(String fileName) throws InvalidFileException;
    boolean deleteFile(String name) throws InvalidFileException;
}
