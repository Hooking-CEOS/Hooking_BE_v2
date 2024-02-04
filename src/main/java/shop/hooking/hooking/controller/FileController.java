package shop.hooking.hooking.controller;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import shop.hooking.hooking.entity.User;
import shop.hooking.hooking.global.jwt.JwtTokenProvider;
import shop.hooking.hooking.service.S3Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController {

    private final S3Service s3service;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Operation(summary = "파일 업로드하기")
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(HttpServletRequest httpRequest, @RequestParam("file") MultipartFile file) {
        try {
            String fileName = file.getOriginalFilename();

            // S3 Presigned URL 및 objectKey 생성
            Map<String, Serializable> presignedUrlInfo = s3service.getPreSignedUrl(httpRequest,fileName);
            String preSignedUrl = removeQueryString(presignedUrlInfo.get("preSignedUrl").toString());
            String objectKey = presignedUrlInfo.get("objectKey").toString();

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());

            // S3에 파일 업로드
            s3service.uploadFileToS3(objectKey, file, metadata);

            return ResponseEntity.ok(preSignedUrl);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private String removeQueryString(String url) {
        int index = url.indexOf("?");
        return (index == -1) ? url : url.substring(0, index);
    }
}
