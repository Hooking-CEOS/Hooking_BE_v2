package shop.hooking.hooking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import shop.hooking.hooking.entity.Review;
import shop.hooking.hooking.entity.User;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findAllByUser(User user); // 작성자아이디로 리뷰 찾기

}
