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
        return hasText(age) ? brand.brandAge.eq(age) : null;
    }

    private BooleanExpression productEq(String product) {
        return hasText(product) ? brand.brandProduct.eq(product) : null;
    }
}

//    private BooleanExpression productEq(List<String> products) {
//        BooleanExpression productExpression = null;
//        if (products != null && !products.isEmpty()) {
//            for (String product : products) {
//                BooleanExpression condition = brand.brandProduct.eq(product);
//                productExpression = (productExpression != null) ? productExpression.or(condition) : condition;
//                // null이라면 첫번째 조건이므로 조건을 할당, null이 아니라면 이전에 생성된 조건에 or처리
//            }
//        }
//        return productExpression;
//    }
//
//    private BooleanExpression ageEq(List<String> ages) {
//        BooleanExpression ageExpression = null;
//        if (ages != null && !ages.isEmpty()) {
//            for (String age : ages) {
//                BooleanExpression condition = brand.brandAge.eq(age);
//                ageExpression = (ageExpression != null) ? ageExpression.or(condition) : condition;
//            }
//        }
//        return ageExpression;
//    }
//
//    private BooleanExpression priceEq(List<String> prices) {
//        BooleanExpression priceExpression = null;
//        if (prices != null && !prices.isEmpty()) {
//            for (String price : prices) {
//                BooleanExpression condition = brand.brandPrice.eq(price);
//                priceExpression = (priceExpression != null) ? priceExpression.or(condition) : condition;
//            }
//        }
//        return priceExpression;
//    }


