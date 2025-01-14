package com.stronglover.services;

import com.stronglover.config.MinioConfig;
import io.minio.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class MinioService {

    private final MinioClient minioClient;
    private final MinioConfig minioConfig;

    //create now bucket if dont found one
    @PostConstruct
    public void init() {
        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder()
                    .bucket(minioConfig.getBucket())
                    .build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder()
                        .bucket(minioConfig.getBucket())
                        .build());
            }
        } catch (Exception e) {
            log.error("Error initializing MinIO bucket", e);
        }
    }
    public void uploadFile(String filename, InputStream inputStream, String contentType) throws Exception {
        minioClient.putObject(PutObjectArgs.builder()
                .bucket(minioConfig.getBucket())
                .object(filename)
                .stream(inputStream, inputStream.available(), -1)
                .contentType(contentType)
                .build());
    }

    public InputStream downloadFile(String filename) throws Exception {
        return minioClient.getObject(GetObjectArgs.builder()
                .bucket(minioConfig.getBucket())
                .object(filename)
                .build());
    }

    public void deleteFile(String filename) throws Exception {
        minioClient.removeObject(RemoveObjectArgs.builder()
                .bucket(minioConfig.getBucket())
                .object(filename)
                .build());
    }
}
