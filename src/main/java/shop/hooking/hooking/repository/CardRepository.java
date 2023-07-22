package shop.hooking.hooking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import shop.hooking.hooking.entity.Brand;
import shop.hooking.hooking.entity.Card;

import java.util.List;


@Repository
public interface CardRepository extends JpaRepository<Card, Long>{

    List<Card> findTop5ByBrandIdOrderByCreatedAtDesc(Long brandId);


    Card findCardByBrandId(Long brandId); //  브랜드 아이디로 카피라이팅 카드 찾기

    @Override
    List<Card> findAll();

    Card findCardById(Long Id);

    List<Card> findCardsByBrandId(Long brandId); //  브랜드 아이디로 카피라이팅 카드 찾기

    //브랜드 네이밍 > 브랜드테이블 브랜드아이디 get > 카드테이블에서 카드 get


    List<Card> findByTextContaining(String q);


}
