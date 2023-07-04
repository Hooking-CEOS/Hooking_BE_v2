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

        String priceString = condition.getPrice();
        String[] prices = null; // null처리 해줘야함
        if (priceString != null) {
            prices = priceString.split(",");
        }

        String ageString = condition.getAge();
        String[] ages = null;
        if (ageString != null) {
            ages = ageString.split(",");
        }


        String productString = condition.getProduct();
        String[] products = null;
        if (productString != null) {
            products = productString.split(",");
        }

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
                        productEq(products),
                        ageEq(ages),
                        priceEq(prices)
                )
                .fetch();
    }

    private BooleanExpression productEq(String[] products) { // 스킨케어, 색조화장
        BooleanExpression productExpression = null;
        if (products != null ) {
            for (String product : products) {
                BooleanExpression condition = brand.brandProduct.eq(product);
                productExpression = (productExpression != null) ? productExpression.or(condition) : condition;
            }
        }
        return productExpression;
    }


    private BooleanExpression ageEq(String[] ages) {
        BooleanExpression ageExpression = null;
        if (ages != null ) {
            for (String age : ages) {
                BooleanExpression condition = brand.brandAge.eq(age);
                ageExpression = (ageExpression != null) ? ageExpression.or(condition) : condition;
            }
        }
        return ageExpression;
    }

    private BooleanExpression priceEq(String[] prices) {
        BooleanExpression priceExpression = null;
        if (prices != null ) {
            for (String price : prices) {
                BooleanExpression condition = brand.brandPrice.eq(price);
                priceExpression = (priceExpression != null) ? priceExpression.or(condition) : condition;
            }
        }
        return priceExpression;
    }
//private BooleanExpression priceEq(String price) {
//    return hasText(price) ? brand.brandPrice.eq(price) : null;
//}
//
//    private BooleanExpression ageEq(String age) {
//        return hasText(age) ? brand.brandAge.eq(age) : null;
//    }
//
//    private BooleanExpression productEq(String product) {
//        return hasText(product) ? brand.brandProduct.eq(product) : null;
//    }

}
