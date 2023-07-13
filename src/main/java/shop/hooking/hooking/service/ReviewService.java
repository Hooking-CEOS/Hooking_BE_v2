package shop.hooking.hooking.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shop.hooking.hooking.dto.response.ReviewRes;
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
    public void writeReview(String title, String content, User user){
        Review review =Review.builder()
                .title(title)
                .content(content)
                .user(user)
                .build();

        reviewRepository.save(review); // 데이터베이스에 저장
    }


    public List<ReviewRes.ReviewDto> getReviewList(User user){ // 반환해줄 값을 content 랑 write
        List<ReviewRes.ReviewDto> reviewDtoList = new ArrayList<>();
        List<Review> reviews = reviewRepository.findAllByUser(user);//데이터베이스에서 리뷰 목록 다 찾아옴
        for(Review review : reviews){
            reviewDtoList.add(ReviewRes.ReviewDto.builder()
                    .writeTime(review.getCreatedTime())
                    .title(review.getTitle())
                    .content(review.getContent())
                    .build());
        }
        return reviewDtoList;
    }
}
