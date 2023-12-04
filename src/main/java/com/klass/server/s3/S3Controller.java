package com.klass.server.s3;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/s3")
public class S3Controller {

    private final S3Service s3Service;

    @Autowired
    public S3Controller(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    @GetMapping("/{fileName}")
    public S3Projection getFileUrl(@PathVariable String fileName) {
        return new S3Projection(s3Service.getFileUrl(fileName));
    }

    @PostMapping(path="/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public S3Projection uploadFile(@ModelAttribute S3File file) throws IOException {
        try {
            return new S3Projection(s3Service.uploadFile(file));
        } catch (IOException e) {
            throw new IOException("Failed to upload file to S3", e);
        }
    }

    @DeleteMapping("/delete/{fileName}")
    public void deleteFile(@PathVariable String fileName) {
        s3Service.deleteFile(fileName);
    }



}
