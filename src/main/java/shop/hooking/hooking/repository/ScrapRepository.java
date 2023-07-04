package shop.hooking.hooking.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import shop.hooking.hooking.entity.Scrap;
import shop.hooking.hooking.entity.User;

import java.util.List;

@Repository
public interface ScrapRepository extends JpaRepository<Scrap, Long> {
    @Override
    List<Scrap> findAll();

    List<Scrap> findScrapByUser(User user);

}