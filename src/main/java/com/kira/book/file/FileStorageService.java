package com.kira.book.file;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class FileStorageService {
    public String saveFile(MultipartFile file, Integer bookId, Integer id) {
    }
}
