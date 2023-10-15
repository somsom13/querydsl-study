package study.querydsl;

import static org.assertj.core.api.Assertions.assertThat;
import static study.querydsl.domain.QOrder.order;
import static study.querydsl.domain.QReview.review;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import study.querydsl.domain.Hello;
import study.querydsl.domain.Menu;
import study.querydsl.domain.Order;
import study.querydsl.domain.OrderMenu;
import study.querydsl.domain.QHello;
import study.querydsl.domain.QReview;
import study.querydsl.domain.Review;
import study.querydsl.repository.MenuRepository;
import study.querydsl.repository.OrderMenuRepository;
import study.querydsl.repository.OrderRepository;
import study.querydsl.repository.ReviewRepository;

@SpringBootTest
//@Transactional
//@Commit
public class QuerydslTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    private JPAQueryFactory jpaQueryFactory;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private OrderMenuRepository orderMenuRepository;

    @Autowired
    private ReviewRepository reviewRepository;


    @Test
    public void testEntity() {
        final Hello hello = new Hello("hi");
        em.persist(hello);

        //확인

        final JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(em);
        final QHello hello1 = QHello.hello;
        final Hello foundHello = jpaQueryFactory.selectFrom(hello1).fetchOne();

        assertThat(foundHello).isEqualTo(hello);
    }

    @Test
    void findAllForSearchWithoutJoin() {
        final Menu menu1 = menuRepository.save(new Menu("menu1", 1000L));
        final Menu menu2 = menuRepository.save(new Menu("menu2", 1000L));
        final Order order1 = orderRepository.save(new Order("order", "cooking"));
        orderMenuRepository.save(new OrderMenu(order1, menu1));
        orderMenuRepository.save(new OrderMenu(order1, menu2));
        final List<Order> all = orderRepository.findAll();
        final List<Order> result = orderRepository.findAllForSearchWithoutJoin("cooking", "menu",
            PageRequest.of(0, 10));
        System.out.println(result);
//        orderRepository.findAll();

    }

    @Test
    void findAllWithJoinForSearch() {
        final Menu menu1 = menuRepository.save(new Menu("menu1", 1000L));
        final Menu menu2 = menuRepository.save(new Menu("menu2", 1000L));
        final Order order1 = orderRepository.save(new Order("order", "cooking"));
        orderMenuRepository.save(new OrderMenu(order1, menu1));
        orderMenuRepository.save(new OrderMenu(order1, menu2));
        final List<Order> all = orderRepository.findAll();
        final List<Order> result = orderRepository.findAllWithJoinForSearch("cooking", "menu",
            PageRequest.of(0, 10));
        System.out.println(result);
//        orderRepository.findAll();

    }

    @Test
    void findAllByLikeCount() {
        final Menu menu1 = menuRepository.save(new Menu("menu1", 1000L));
        menu1.updateLikeCount();
        menu1.updateLikeCount();
        menu1.updateLikeCount();
        menuRepository.save(menu1);
        final Order order1 = orderRepository.save(new Order("order", "cooking"));
        orderMenuRepository.save(new OrderMenu(order1, menu1));

        final Menu menu2 = menuRepository.save(new Menu("menu1", 1000L));
        final Order order2 = orderRepository.save(new Order("order", "cooking"));
        orderMenuRepository.save(new OrderMenu(order2, menu2));

        final Menu menu3 = menuRepository.save(new Menu("menu1", 1000L));
        final Order order3 = orderRepository.save(new Order("order", "cooking"));
        orderMenuRepository.save(new OrderMenu(order3, menu3));

        final Menu menu4 = menuRepository.save(new Menu("menu1", 1000L));
        final Order order4 = orderRepository.save(new Order("order", "cooking"));
        orderMenuRepository.save(new OrderMenu(order4, menu4));

        final Menu menu5 = menuRepository.save(new Menu("menu1", 1000L));
        final Order order5 = orderRepository.save(new Order("order", "cooking"));
        orderMenuRepository.save(new OrderMenu(order5, menu5));

        final List<Order> allByLikeCount = orderRepository.findAllByLikeCount(order4.getId(),
            PageRequest.of(0, 10));
        System.out.println();
    }

    @Test
    void findAllByRate() {
        final Order order1 = orderRepository.save(new Order("order", "cooking"));

        final Order order2 = orderRepository.save(new Order("order", "cooking"));
        final Review review2 = reviewRepository.save(new Review("review2", 10, order2));

        final Order order3 = orderRepository.save(new Order("order", "cooking"));
        final Review review3 = reviewRepository.save(new Review("review3", 5, order3));

        final Order order4 = orderRepository.save(new Order("order", "cooking"));
        final Review review4 = reviewRepository.save(new Review("review4", 7, order4));

        final Order order5 = orderRepository.save(new Order("order", "cooking"));

        // order4를 기준으로 조회 => order3, order1, order5 조회

        final List<Order> allByRate = orderRepository.findAllByRate(order4.getId(),
            PageRequest.of(0, 10));
        System.out.println("allByRate.size() = " + allByRate.size());
    }

    @Test
    void jpqlCompileExceptionTest() {
        findAllByFilter(null, "cooking");
    }

    private void findAllByFilter(final String name, final String orderStatus) {
        String query = "select o from Order o";
        String wherePredicate1 = "where o.orderStatus = :orderStatus;";
        String wherePredicate2 = "where o.name = :name;";

        if (name != null) {
            query += wherePredicate2;
        }
        if (orderStatus != null) {
            query += wherePredicate1;
        }
        System.out.println("query = " + query);
//        actual query = "select o from Order owhere o.orderStatus=:orderStatus"
//        expected query = "select o from Order o where o.orderStatus = :orderStatus";
        final List<Order> result = em.createQuery(query, Order.class)
            .setParameter("orderStatus", orderStatus).getResultList();
    }

    @Test
    void querydslTypeSafeTest(final String name, final String orderStatus) {
        final List<Order> fetch = jpaQueryFactory.selectFrom(order)
            .where(predicateOrderStatus(orderStatus), predicateName(name))
            .fetch();
        // 애초에 query 문법 오류가 나는 상황이 생기지 않는다.
    }

    private BooleanExpression predicateOrderStatus(final String orderStatus) {
        if (orderStatus == null) {
            return null;
        }
        return order.orderStatus.eq(orderStatus);
    }

    private BooleanExpression predicateName(final String name) {
        if (name == null) {
            return null;
        }
        return order.name.eq(name);
    }

    @Test
    void joinOneToManyWithoutGroupBy() {
        final Order order1 = orderRepository.save(new Order("order", "cooking"));

        final Order order2 = orderRepository.save(new Order("order", "cooking"));
        final Review review2 = reviewRepository.save(new Review("review2", 10, order2));
        final Review review4 = reviewRepository.save(new Review("review4", 10, order2));

        final Order order3 = orderRepository.save(new Order("order", "cooking"));
        final Review review3 = reviewRepository.save(new Review("review3", 5, order3));
        final Review review5 = reviewRepository.save(new Review("review3", 5, order3));
        final Review review6 = reviewRepository.save(new Review("review3", 5, order3));

        final List<Order> fetch = jpaQueryFactory.selectFrom(order)
            .leftJoin(order.reviews, review)
            .fetchJoin()
            .where(review.content.contains("review").or(review.rate.eq(5)))
//            .fetchJoin()
            .fetch();


        System.out.println("fetch.size() = " + fetch.size());
    }

    @Test
    void createSubQueryUsingJpaExpressions() {
        final long id = 1L;
        final QReview review2 = new QReview("review2");
        final NumberExpression<Integer> otherOrderTotalRate = calculateTotalRate(review);

        final List<Order> result = jpaQueryFactory.selectFrom(order)
            .leftJoin(order.reviews, review)
            .groupBy(order.id)
            .having(
                otherOrderTotalRate
                    .lt(
                        JPAExpressions.select(calculateTotalRate(review2))
                            .from(review2).
                            where(review2.order.id.eq(id)))
                    .or(otherOrderTotalRate
                        .eq(
                            JPAExpressions.select(calculateTotalRate(review2))
                                .from(review2)
                                .where(review2.order.id.eq(id)))
                        .and(order.id.lt(id))
                    )
            )
            .fetch();
    }

    private NumberExpression<Integer> calculateTotalRate(final QReview review) {
        return review.rate.coalesce(0).sum();
    }

}
