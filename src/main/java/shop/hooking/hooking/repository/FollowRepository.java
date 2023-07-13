package shop.hooking.hooking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.hooking.hooking.entity.Brand;
import org.springframework.stereotype.Repository;
import shop.hooking.hooking.entity.Follow;
import shop.hooking.hooking.entity.User;

import java.util.List;


@Repository
public interface FollowRepository extends JpaRepository<Follow, Long>{

    boolean existsByBrandAndUser(Brand brand, User user);


}
