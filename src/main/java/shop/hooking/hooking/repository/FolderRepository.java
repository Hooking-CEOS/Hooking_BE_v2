package shop.hooking.hooking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.hooking.hooking.entity.Folder;
import shop.hooking.hooking.entity.User;

import java.util.*;

public interface FolderRepository extends JpaRepository<Folder, Long>{

    Optional<Folder> findFolderById(Long aLong);

    List<Folder> findByUser(User user);
}
