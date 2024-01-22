package shop.hooking.hooking.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import shop.hooking.hooking.entity.User;
import shop.hooking.hooking.global.jwt.JwtTokenProvider;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 amazonS3;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    private final JwtTokenProvider jwtTokenProvider;

    public Map<String, Serializable> getPreSignedUrl(HttpServletRequest httpRequest, String fileName) {
        try {
            User user = jwtTokenProvider.getUserInfoByToken(httpRequest);
            String encodedFileName = String.format("%s_%s", fileName, LocalDateTime.now());
            String objectKey = user.getKakaoId()+ "/" + encodedFileName;

            Date expiration = new Date();
            long expTimeMillis = expiration.getTime();
            expTimeMillis += (3 * 60 * 1000); // 3분
            expiration.setTime(expTimeMillis); // URL 만료 시간 설정

            GeneratePresignedUrlRequest generatePresignedUrlRequest =
                    new GeneratePresignedUrlRequest(bucket, objectKey)
                            .withMethod(HttpMethod.PUT)
                            .withExpiration(expiration);

            return Map.of(
                    "preSignedUrl", amazonS3.generatePresignedUrl(generatePresignedUrlRequest),
                    "objectKey", objectKey
            );
        } catch (Exception e) {
            // 예외 처리를 적절히 수행
            e.printStackTrace();
            return null;
        }
    }

    public void uploadFileToS3(String objectKey, MultipartFile file, ObjectMetadata metadata) throws IOException {
        amazonS3.putObject(bucket, objectKey, file.getInputStream(), metadata);
    }
}
