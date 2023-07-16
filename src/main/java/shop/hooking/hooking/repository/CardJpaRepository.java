package shop.hooking.hooking.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;
import shop.hooking.hooking.dto.CardSearchCondition;
import shop.hooking.hooking.dto.response.CopyRes;
import shop.hooking.hooking.dto.response.QCopyRes;
import shop.hooking.hooking.entity.*;

import javax.persistence.EntityManager;
import java.util.List;
import static shop.hooking.hooking.entity.QBrand.brand;
import static shop.hooking.hooking.entity.QCard.card;
import static shop.hooking.hooking.entity.QHave.have;
import static shop.hooking.hooking.entity.QMood.mood;
@Repository
public class CardJpaRepository {

    private final EntityManager em;

    private final JPAQueryFactory queryFactory;

    public CardJpaRepository(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    public List<CopyRes> search(CardSearchCondition condition){

        String moodString = condition.getMood();
        String[] moods = null;
        if (moodString != null) {
            moods = moodString.split(",");
        }

        String priceString = condition.getPrice();
        String[] prices = null;
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

        QHave have = QHave.have;
        QBrand brand = QBrand.brand;
        QMood mood = QMood.mood;



        return queryFactory
                .selectDistinct(new QCopyRes(
                        card.id,
                        card.brand,
                        card.text,
                        card.scrapCnt,
                        card.createdAt))
                .from(card)
                .leftJoin(card.brand, brand) // card, brand 조인
                .leftJoin(have) // brand, have 조인
                .on(have.brand.eq(brand))
                .join(have.mood, mood) // have와 mood 조인
                .where(
                        productEq(products),
                        ageEq(ages),
                        priceEq(prices),
                        moodEq(moods)
                )
                .fetch();
    }



    private BooleanExpression moodEq(String[] moods) {
        BooleanExpression moodExpression = null;
        if (moods != null ) {
            for (String mood : moods) {
                BooleanExpression condition = QMood.mood.moodName.eq(mood);
                moodExpression = (moodExpression != null) ? moodExpression.or(condition) : condition;
            }
        }
        return moodExpression;
    }

    private BooleanExpression productEq(String[] products) {
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

}