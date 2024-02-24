package shop.hooking.hooking.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;
import shop.hooking.hooking.dto.CardSearchCondition;
import shop.hooking.hooking.dto.response.CopyResDto;
import shop.hooking.hooking.dto.response.QCopyResDto;
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

    public List<CopyResDto> filter(CardSearchCondition condition){

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
                .selectDistinct(new QCopyResDto(
                        card.id,
                        card.brand,
                        card.text,
                        card.scrapCnt,
                        card.createdAt,
                        card.url))
                .from(card)
                .leftJoin(card.brand, brand)
                .leftJoin(have)
                .on(have.brand.eq(brand))
                .join(have.mood, mood)
                .where(
                        productEq(products),
                        ageEq(ages),
                        priceEq(prices),
                        moodEq(moods)
                )
                .fetch();
    }



    public List<CopyResDto> searchMood(String q){
        BooleanExpression moodNameEqualsQ = mood.moodName.eq(q);

        return queryFactory
                .selectDistinct(new QCopyResDto(
                        card.id,
                        card.brand,
                        card.text,
                        card.scrapCnt,
                        card.createdAt,
                        card.url))
                .from(card)
                .leftJoin(card.brand, brand)
                .leftJoin(have)
                .on(have.brand.eq(brand))
                .join(have.mood, mood)
                .where(moodNameEqualsQ)
                .fetch();
    }

    public List<CopyResDto> searchCopy(String q){
        BooleanExpression textContainsQ = card.text.contains(q);

        return queryFactory
                .selectDistinct(new QCopyResDto(
                        card.id,
                        card.brand,
                        card.text,
                        card.scrapCnt,
                        card.createdAt,
                        card.url))
                .from(card)
                .where(textContainsQ)
                .fetch();
    }

    public List<CopyResDto> searchBrand(String q){
        BooleanExpression brandContainsQ = brand.brandName.contains(q);

        return queryFactory
                .selectDistinct(new QCopyResDto(
                        card.id,
                        card.brand,
                        card.text,
                        card.scrapCnt,
                        card.createdAt,
                        card.url))
                .from(card)
                .leftJoin(card.brand, brand)
                .where(brandContainsQ)
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