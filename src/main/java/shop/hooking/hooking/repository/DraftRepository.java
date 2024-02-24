package shop.hooking.hooking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.hooking.hooking.entity.Draft;
import shop.hooking.hooking.entity.User;

import java.util.List;
import java.util.Optional;
public interface DraftRepository extends JpaRepository<Draft, Long>{

    List<Draft> findByUser(User user);


    List<Draft> findBydraftId(Long id);
}
