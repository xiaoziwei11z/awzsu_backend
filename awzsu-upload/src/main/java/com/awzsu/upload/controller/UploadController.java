package com.awzsu.upload.controller;

import com.awzsu.common.entity.Result;
import com.awzsu.common.entity.StatusCode;
import com.awzsu.upload.service.UploadService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@CrossOrigin
@RequestMapping("/upload")
public class UploadController {
    @Autowired
    private UploadService uploadService;

    /**
     * 图片上传
     * @param imageFile
     * @return
     * @throws IOException
     */
    @PostMapping("/image")
    public Result uploadImage(@RequestParam("file") MultipartFile imageFile) throws IOException {
        String url = uploadService.uploadImage(imageFile);
        if(StringUtils.isBlank(url)){
            return new Result(false, StatusCode.ERROR,"上传失败");
        }else{
            return new Result(true, StatusCode.OK,"上传成功",url);
        }
    }
}
