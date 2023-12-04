package com.klass.server.s3;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class S3Service {
    @Value("${aws.s3.bucket.name}")
    private String bucketName;

    private final S3Client s3Client;

    // Generate file name
    private String generateFileName(MultipartFile file) {
        return new Date().getTime() + "-"
                + Objects.requireNonNull(file.getOriginalFilename())
                .replace(" ", "_");
    }

    // Ger download url for file
    public String getFileUrl(String fileName) {
        try {
            GetUrlRequest request = GetUrlRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();

            return s3Client.utilities().getUrl(request).toString();
        } catch (S3Exception e) {
            throw new IllegalStateException("Failed to get file from S3", e);
        }
    }

    // Upload file to S3
    public String uploadFile(S3File file) throws IOException {
        // Multipart to File
        File convertedFile = new File(Objects.requireNonNull(file.getFile().getOriginalFilename()));
        try (FileOutputStream fileOutputStream = new FileOutputStream(convertedFile)){
            fileOutputStream.write(file.getFile().getBytes());
        }

        // generate convertedFile name
        String fileName = file.getKeyPath() + generateFileName(file.getFile());

        try {
            Map<String, String> metadata = new HashMap<>();
            metadata.put("x-amz-meta-title", file.getFile().getOriginalFilename());
            // upload convertedFile
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .contentType(file.getFile().getContentType())
                    .metadata(metadata)
                    .acl("public-read")
                    .build();
            // Send to S3 client
            s3Client.putObject(request, RequestBody.fromFile(convertedFile));
        } catch (S3Exception e) {
            throw new IOException("Failed to upload convertedFile to S3", e);
        }

        // delete convertedFile
        boolean delete = convertedFile.delete();
        if (!delete) {
            throw new IOException("Failed to delete temp convertedFile");
        }

        return getFileUrl(fileName);
    }

    // Delete file from S3
    public void deleteFile(String fileName) {
        try {
            DeleteObjectRequest request = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();

            s3Client.deleteObject(request);
        } catch (S3Exception e) {
            throw new IllegalStateException("Failed to delete file from S3", e);
        }
    }



}
