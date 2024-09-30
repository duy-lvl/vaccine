package fa.training.backend.service.impl;

import com.google.cloud.storage.*;
import com.google.firebase.cloud.StorageClient;
import fa.training.backend.exception.common.InvalidFileException;
import fa.training.backend.service.FirebaseService;
import fa.training.backend.util.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class FirebaseServiceImpl implements FirebaseService {
    @Value("${firebase.bucket.name}")
    private String bucketName;
    @Value("${firebase.url}")
    private String firebaseUrl;

    @Override
    public String upload(MultipartFile file) throws IOException {
        Bucket bucket = StorageClient.getInstance().bucket();

        String name = UUID.randomUUID() + "_" + file.getOriginalFilename();

        bucket.create(name, file.getBytes(), file.getContentType());

        return name;
    }

    @Override
    public URL getFileUrl(String fileName) throws InvalidFileException {
        Storage storage = StorageClient.getInstance().bucket().getStorage();

        Blob blob = storage.get(bucketName, fileName);
        if (blob == null) {
            throw new InvalidFileException(Message.MSG_106);
        }

        return blob.signUrl(1, TimeUnit.DAYS, Storage.SignUrlOption.withV4Signature());
    }

    @Override
    public boolean deleteFile(String name) throws InvalidFileException{
        Bucket bucket = StorageClient.getInstance().bucket();
        if (name.isEmpty()) {
            throw new InvalidFileException(Message.MSG_103);
        }
        Blob blob = bucket.get(name);
        if (blob == null) {
            throw new InvalidFileException(Message.MSG_106);
        }
        return blob.delete();
    }
}
