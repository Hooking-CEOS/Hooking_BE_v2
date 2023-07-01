package shop.hooking.hooking.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shop.hooking.hooking.dto.review.ReviewRes;
import shop.hooking.hooking.entity.Review;
import shop.hooking.hooking.entity.User;
import shop.hooking.hooking.repository.ReviewRepository;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

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


    public List<ReviewRes.ReviewDto> getReviewList(Long writerId){ // 반환해줄 값을 content 랑 write
        List<ReviewRes.ReviewDto> reviewDtoList = new ArrayList<>();
        List<Review> reviews = reviewRepository.findAllByWriterId(writerId);//데이터베이스에서 리뷰 목록 다 찾아옴
        for(Review review : reviews){
            reviewDtoList.add(ReviewRes.ReviewDto.builder()
                    .writeTime(review.getCreatedTime())
                    .content(review.getContent())
                    .build());
        }
        return reviewDtoList;
    }
}
