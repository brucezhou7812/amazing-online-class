package nz.co.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    String uploadUserImage(MultipartFile file);
}
