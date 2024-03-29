package shop.hooking.hooking.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;
import shop.hooking.hooking.entity.Draft;
import shop.hooking.hooking.entity.QDraft;
import shop.hooking.hooking.entity.User;

import javax.persistence.EntityManager;
import java.util.List;


@Repository
public class DraftJpaRepository {

    private final EntityManager em;

    private final JPAQueryFactory queryFactory;

    public DraftJpaRepository(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }


    public List<Draft> searchUserDraft(User user, String q) {
        BooleanExpression draftContainQ = QDraft.draft.user.eq(user)
                .and(QDraft.draft.text.contains(q));

        return queryFactory
                .selectDistinct(QDraft.draft)
                .from(QDraft.draft)
                .where(draftContainQ)
                .fetch();
    }



}
