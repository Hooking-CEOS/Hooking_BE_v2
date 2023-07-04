package shop.hooking.hooking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import shop.hooking.hooking.entity.Card;

import java.util.List;


@Repository
public interface CardRepository extends JpaRepository<Card, Long>{

    List<Card> findCardsByBrandId(Long brandId); //  브랜드 아이디로 카피라이팅 카드 찾기


}
