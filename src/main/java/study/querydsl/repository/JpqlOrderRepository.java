package study.querydsl.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import org.springframework.stereotype.Repository;
import study.querydsl.domain.Order;

@Repository
public class JpqlOrderRepository {

    @PersistenceContext
    private EntityManager em;

    public List<Order> findAllByFilter(final String name, final String orderStatus) {
        String query = "select o from Order o";
        String wherePredicate1 = "where o.orderStatus = :orderStatus";
        String wherePredicate2 = "where o.name = :name";

        if (name != null) {
            query += wherePredicate2;
        }
        if (orderStatus != null) {
            query += wherePredicate1;
        }
        //실수 : 실제 쿼리는 "select m from member mwhere m.username=:username"
        return em.createQuery(query, Order.class).getResultList();
    }
}
