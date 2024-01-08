package shop.hooking.hooking.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

@SpringBootTest
@Transactional
public class CardJpaRepositoryTest {

    @Autowired
    EntityManager em;

    @Autowired
    private final CardJpaRepository cardJpaRepository;

    public CardJpaRepositoryTest(CardJpaRepository cardJpaRepository) {
        this.cardJpaRepository = cardJpaRepository;
    }

}
