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

    List<Scrap> findScrapByUser(User user);

<<<<<<< HEAD

    List<Scrap> findByCardId(Long id);

    Scrap findByUserAndCardId(User user, Long cardId);

    boolean existsByUserAndCard(User user, Card card);
=======

    List<Scrap> findByCardId(Long id);

>>>>>>> e9b55d261bb4f0caeb80f1c90c39e0b37859dcbc

    Scrap findByUserAndCard(User user, Card card);


}