package shop.hooking.hooking.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shop.hooking.hooking.entity.Review;
import shop.hooking.hooking.entity.User;
import shop.hooking.hooking.repository.ReviewRepository;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;

    @Transactional
    public void writeReview(String content, Long writerId){
        Review review =Review.builder()
                .content(content)
                .writerId(writerId)
                .build();

        reviewRepository.save(review); // 데이터베이스에 저장

    }
}
