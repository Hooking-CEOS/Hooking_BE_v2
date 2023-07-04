package shop.hooking.hooking.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import shop.hooking.hooking.dto.CardSearchCondition;
import shop.hooking.hooking.dto.response.CopyRes;
import shop.hooking.hooking.entity.Brand;
import shop.hooking.hooking.entity.Card;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;

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
