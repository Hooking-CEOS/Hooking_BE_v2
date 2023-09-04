package shop.hooking.hooking.repository;


import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import shop.hooking.hooking.entity.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findMemberByKakaoId(Long kakaoId);

    User findUserByUserId(Long Id);

    Optional<User> findByEmail(@Param("email") String email);
}



