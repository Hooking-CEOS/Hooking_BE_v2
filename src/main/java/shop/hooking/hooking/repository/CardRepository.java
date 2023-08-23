package shop.hooking.hooking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import shop.hooking.hooking.entity.Brand;
import shop.hooking.hooking.entity.Card;

import java.util.List;


@Repository
public interface CardRepository extends JpaRepository<Card, Long>{

    List<Card> findTop6ByBrandIdOrderByCreatedAtDesc(Long brandId);

    @Override
    List<Card> findAll();

    Card findCardById(Long Id);

    List<Card> findCardsByBrandId(Long brandId);
<<<<<<< HEAD
=======


>>>>>>> e9b55d261bb4f0caeb80f1c90c39e0b37859dcbc

}
