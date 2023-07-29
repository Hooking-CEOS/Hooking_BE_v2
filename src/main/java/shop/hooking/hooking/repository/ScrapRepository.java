package shop.hooking.hooking.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import shop.hooking.hooking.entity.Card;
import shop.hooking.hooking.entity.Scrap;
import shop.hooking.hooking.entity.User;

import java.util.List;

@Repository
public interface ScrapRepository extends JpaRepository<Scrap, Long> {
    @Override
    List<Scrap> findAll();

    List<Scrap> findScrapByUser(User user); //여기서 최신순으로 가져오게

    Scrap findByUserAndCardId(User user, Long cardId);

    List<Scrap> findByCardId(Long id); // 카드 식별번호로 스크랩 객체 찾기

    boolean existsByUserAndCard(User user, Card card); // 유저와 카드로 존재 여부 확인하기

    Scrap findByUserAndCard(User user, Card card);

}