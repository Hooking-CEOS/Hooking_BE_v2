package shop.hooking.hooking.repository;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;
import shop.hooking.hooking.dto.CardSearchCondition;
import shop.hooking.hooking.dto.response.CopyRes;
import shop.hooking.hooking.dto.response.QCopyRes;

import javax.persistence.EntityManager;
import java.util.List;

import static org.springframework.util.StringUtils.hasText;
import static shop.hooking.hooking.entity.QBrand.brand;
import static shop.hooking.hooking.entity.QCard.card;

@Repository
public class CardJpaRepository {

    private final EntityManager em;

    private final JPAQueryFactory queryFactory;


    public CardJpaRepository(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    public List<CopyRes> search(CardSearchCondition condition){
        return queryFactory
                .select(new QCopyRes(
                        card.id,
                        card.brand,
                        card.text,
                        card.scrapCnt,
                        card.createdAt))
                .from(card)
                .leftJoin(card.brand, brand) // 조인
                .where(
                        productEq(condition.getProduct()),
                        ageEq(condition.getAge()),
                        priceEq(condition.getPrice())
                )
                .fetch();
    }

    private BooleanExpression priceEq(String price) {
        return hasText(price) ? brand.brandPrice.eq(price) : null;
    }

    private BooleanExpression ageEq(String age) {
        return hasText(age) ? brand.brandPrice.eq(age) : null;
    }

    private BooleanExpression productEq(String product) {
        return hasText(product) ? brand.brandPrice.eq(product) : null;
    }
}
