package com.awzsu.upload.service;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class UploadService {
    @Autowired
    private FastFileStorageClient fastFileStorageClient;

    public String uploadImage(MultipartFile imageFile) throws IOException {
        String ext = StringUtils.substringAfterLast(imageFile.getOriginalFilename(),".");
        StorePath storePath = fastFileStorageClient.uploadFile(imageFile.getInputStream(), imageFile.getSize(), ext, null);
        return "http://image.awzsu.com/"+storePath.getFullPath();
    }
}
