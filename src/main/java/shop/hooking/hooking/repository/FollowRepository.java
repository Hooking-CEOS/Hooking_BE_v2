package shop.hooking.hooking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.hooking.hooking.entity.Brand;
import org.springframework.stereotype.Repository;
import shop.hooking.hooking.entity.Follow;

import java.util.List;


@Repository
public interface FollowRepository extends JpaRepository<Follow, Long>{


}
