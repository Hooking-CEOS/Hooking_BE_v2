package shop.hooking.hooking.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import shop.hooking.hooking.entity.Contain;
import shop.hooking.hooking.entity.Scrap;
import shop.hooking.hooking.entity.User;

import java.util.List;
import java.util.Optional;

public interface ContainRepository extends JpaRepository<Contain, Long> {


    List<Contain> findByFolderId(Long folderId);
}
