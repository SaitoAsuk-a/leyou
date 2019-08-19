package com.leyou.controller;

import com.leyou.service.UploadService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author: taft
 * @Date: 2018-8-17 17:09
 */
@RestController
@RequestMapping("upload")
public class UploadController {



    @Autowired
    private UploadService uploadService;

    @PostMapping("image")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file){

        String url = uploadService.upload(file);

        if (!StringUtils.isNotBlank(url)){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok(url);
    }
}
