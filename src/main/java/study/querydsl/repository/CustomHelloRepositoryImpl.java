package study.querydsl.repository;

import static study.querydsl.domain.QHello.hello;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import study.querydsl.domain.Hello;

// 왜 repository 안붙여도 잘 되지?
@RequiredArgsConstructor
public class CustomHelloRepositoryImpl implements CustomHelloRepository {

    // jpaQueryFactory 는 반드시 bean으로 등록해두어야 한다.
    private final JPAQueryFactory jpaQueryFactory;
    private final EntityManager entityManager;


    public Object findIdByJpaQuery() {
        JPAQuery query = new JPAQuery(entityManager);
        return query.select(hello.id)
            .from(hello)
            .fetchOne();
    }

    @Override
    public Long findId() {
        final JPAQuery<Long> select = jpaQueryFactory.select(hello.id);

        return jpaQueryFactory.select(hello.id)
            .from(hello)
            .fetchOne();
    }

    public List<Hello> findAllByJpaQuery() {
        JPAQuery<Hello> query = new JPAQuery<>(entityManager);
        return query.from(hello).fetchAll().fetch();
    }

    public List<Hello> findByAllWithPredicateOrderLimit() {
        return jpaQueryFactory.selectFrom(hello)
            .orderBy(pageableToSortCondition(Sort.by(Direction.ASC, "id")))
            .where(hello.name.contains("n"))
            .limit(10)
            .fetch();
    }

    public Page<Hello> findAllByPageable(final Pageable pageable) {
        final List<Hello> resultsForCurrentPage = jpaQueryFactory.selectFrom(hello)
            .where(hello.name.contains("hi"))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch(); // 요청된 Page 정보에 맞는 데이터들이 담긴다.

        final Long totalDataCount = jpaQueryFactory.select(hello.count())
            .from(hello)
            .where(hello.name.contains("hi"))
            .fetchOne();

        return new PageImpl<>(resultsForCurrentPage, pageable, totalDataCount);
        // 이번 페이지에 들어가는 데이터, Pageable 정보, 페이지 번호 확인을 위한 전체 데이터 개수
    }

    // PageRequest.of(0, 10, Sort.by(Order.DESC, "id"));
    public Page<Hello> findAllByPageableIncludingSort(final Pageable pageable) {
        // orderBy(OrderSpecifier) -> Pageable로부터 OrderSpecifier를 만들어주어야 한다.
        final List<Hello> resultsForCurrentPage = jpaQueryFactory.selectFrom(hello)
            .where(hello.name.contains("hi"))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(pageableToSortCondition(pageable.getSort()))
            .fetch(); // 요청된 Page 정보에 맞는 데이터들이 담긴다.

        final Long totalDataCount = jpaQueryFactory.select(hello.count())
            .from(hello)
            .where(hello.name.contains("hi"))
            .fetchOne();

        return new PageImpl<>(resultsForCurrentPage, pageable, totalDataCount);
    }

    private OrderSpecifier[] pageableToSortCondition(final Sort sort) {
        final List<OrderSpecifier> orders = new ArrayList<>();
        if (sort.isEmpty()) {
            return null; // orderBy는 파라미터로 null을 받으면 NPE가 발생한다. -> 처리 필요
        }
        for (final Sort.Order order : sort) {
            Order sortDirection = Order.ASC;
            if (order.isDescending()) {
                sortDirection = Order.DESC;
            }
            final String orderTarget = order.getProperty();
            if (orderTarget.equals("id")) {
                orders.add(new OrderSpecifier(sortDirection, hello.id));
                continue;
            }
            // 약속된 정렬 기준에 해당하지 않는 값이 들어왔다면 이에 대한 처리 필요
        }
        return orders.toArray(new OrderSpecifier[0]);
    }

    @Override
    public List<Hello> findAllOrderById() {
        return jpaQueryFactory.selectFrom(hello)
            .where(hello.name.containsIgnoreCase("hi"))
            .orderBy(new OrderSpecifier<>(Order.DESC, hello.id))
            .fetch();
    }

    @Override
    public Long findUsingNickname(final String name) {
        return jpaQueryFactory.select(hello.id)
            .from(hello)
            .where(hello.name.eq(name))
            .fetchOne();
    }
}
