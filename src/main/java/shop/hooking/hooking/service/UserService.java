package shop.hooking.hooking.service;


import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import shop.hooking.hooking.dto.OAuthAttributesDTO;
//import shop.hooking.hooking.dto.response.UserResponseDTO;
import shop.hooking.hooking.entity.User;
import shop.hooking.hooking.exception.CustomException;
import shop.hooking.hooking.exception.ErrorCode;
import shop.hooking.hooking.repository.UserRepository;


import javax.transaction.Transactional;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;



//@Service
//@RequiredArgsConstructor
//public class UserService {
//    private final UserRepository userRepository;
//
//    @Transactional
//    public OAuthAttributesDTO findSessionUser(Long Id) {
//        User user = findUserEntity(Id);
//        String imageUrl;
//        if(user.getImage().contains("k.kakaocdn.net")) {
//            imageUrl = user.getImage();
//        }
//        else {
//            imageUrl = findProfileImage(Id);
//        }
//        return new OAuthAttributesDTO(user, imageUrl);
//    }
//
//    @Transactional
//    public OAuthAttributesDTO findUser(Long Id) {
//        User user = findUserEntity(Id);
//        String imageUrl;
//        if(user.getImage().contains("k.kakaocdn.net")) {
//            imageUrl = user.getImage();
//        }
//        else {
//            imageUrl = findProfileImage(Id);
//        }
//        return new OAuthAttributesDTO(user, imageUrl);
//    }
//
//
//
//    @Transactional
//    public String findProfileImage(Long Id) {
//        User user = findUserEntity(Id);
//        String fileName = User.getImage();
//
//        S3Presigner presigner = ImageUploadService.createPresigner(properties.getCredentials().getAccessKey(), properties.getCredentials().getSecretKey());
//
//        String url = ImageUploadService.getS3DownloadURL(presigner, properties.getS3().getBucket(), fileName);
//        presigner.close();
//        return url;
//    }
//
//
//
//    public User findUserEntity(Long memberId) {
//        return userRepository.findMemberByMemberIdAndDeleteFlagIsFalse(memberId)
//                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND, null));
//    }
//
//
//}