package study.querydsl.repository;

import static com.querydsl.core.types.Order.ASC;
import static com.querydsl.core.types.Order.DESC;
import static study.querydsl.domain.QMenu.menu;
import static study.querydsl.domain.QOrder.order;
import static study.querydsl.domain.QOrderMenu.orderMenu;
import static study.querydsl.domain.QReview.review;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import study.querydsl.domain.Order;
import study.querydsl.domain.QReview;

@RequiredArgsConstructor
@Repository
public class CustomOrderRepositoryImpl implements CustomOrderRepository {

    private final JPAQueryFactory jpaQueryFactory;

    // TODO: 2023-10-14 명시적 join VS 묵시적 join
    @Override
    public List<Order> findAllForSearchWithoutJoin(final String orderStatus,
                                                   final String menuKeyword,
                                                   final Pageable pageable) {
        return jpaQueryFactory.selectFrom(order)
            .where(isStatus(orderStatus).or(hasMenuMatching(menuKeyword)) ) // 명시적 join을 안했을 때
            .orderBy(pageableToSortCondition(pageable))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();
    }

    @Override
    public List<Order> findAll() {
        return jpaQueryFactory.selectFrom(order)
            .fetch();
        /**
         * 빌더 방식으로 구성
         * SQL과 유사한 구조로 SELECT 할 컬럼, 대상 테이블, where 조건절이 들어감
         * java로 구문 오류를 최소화하면서도 익숙한 SQL처럼 한 눈에 이해되는 쿼리를 작성할 수 있다.
         */
    }

    // TODO: 2023-10-14 명시적 join VS 묵시적 join
    @Override
    public List<Order> findAllWithJoinForSearch(final String orderStatus, final String menuKeyword,
                                                final Pageable pageable) {
        return jpaQueryFactory.selectFrom(order)
            .join(order.orderMenus, orderMenu)
//            .fetchJoin() // fetchJoin 안 했을 때는 어떻게 쿼리발생하고, 어떻게 조회되는지
            .join(orderMenu.menu, menu)
//            .fetchJoin()
            .where(isStatus(orderStatus), hasMenuMatchingByUsingJoin(menuKeyword))
            .orderBy(pageableToSortCondition(pageable))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();
    }



    // TODO: 2023-10-14 QueryDsl 가독성 향상
    @Override
    public List<Order> findAllByRate(final long id, final Pageable pageable) {
        return jpaQueryFactory.selectFrom(order)
            .leftJoin(order.reviews, review) // reviews가 없어도 order는 있어야 한다.
            .groupBy(order.id)
            .having(whereRateIsLessThan(id))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(review.rate.sum().asc(), order.id.asc())
            .fetch();
    }

    private BooleanExpression whereRateIsLessThan(final long id) {
        final NumberExpression<Integer> otherOrderTotalRate = calculateTotalRate();
        final JPAQuery<Integer> currentOrderTotalRate = getTotalRateOfOrderById(id);

        return otherOrderTotalRate.lt(currentOrderTotalRate)
            .or(otherOrderTotalRate.eq(currentOrderTotalRate).and(order.id.lt(id)));
    }

    private NumberExpression<Integer> calculateTotalRate() {
        return review.rate.coalesce(0).sum();
//        return review.rate.coalesce(0).sum().divide(100).floor();
    }

    private JPAQuery<Integer> getTotalRateOfOrderById(final long id) {
        final QReview review2 = new QReview("review2");

        return jpaQueryFactory.select(calculateTotalRate())
            .from(review2)
            .where(review2.order.id.eq(id));
    }

    @Override
    public List<Order> findAllByLikeCount(final long id, final Pageable pageable) {
        /**
         * "SELECT s FROM Song s "
         *         + "LEFT JOIN s.killingParts.killingParts kp "
         *         + "GROUP BY s.id "
         *         + "HAVING SUM(COALESCE(kp.likeCount, 0)) < (SELECT SUM(COALESCE(kp2.likeCount, 0)) FROM KillingPart kp2 WHERE kp2.song.id = :id) "
         *         + "OR (SUM(COALESCE(kp.likeCount, 0)) = (SELECT SUM(COALESCE(kp3.likeCount, 0)) FROM KillingPart kp3 WHERE kp3.song.id = :id) AND s.id < :id) "
         *         + "ORDER BY SUM(COALESCE(kp.likeCount, 0)) DESC, s.id DESC;
         */
        return jpaQueryFactory.selectFrom(order)
            .leftJoin(order.orderMenus, orderMenu)
            .leftJoin(orderMenu.menu, menu)
            .groupBy(order.id)
            .having(whereLikeCountIsLessThan(id))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(menu.likeCount.sum().desc())
            .fetch();
    }

    private BooleanExpression whereLikeCountIsLessThan(final long currentId) {
        /**
         * 개신기한 것: 앞에서 join을 하면, 호출되는 메서드에서는 join된 대상에 대해 바로 접근할 수 있다. (굳이 order.id.으로 접근 안해도 됨)
         * join앞에서 하는 것과, 뒤에서 order.id.어쩌구로 접근하는 것의 차이
         */
        final NumberExpression<Long> likeCountToCompare = menu.likeCount.sum(); // 부모 메서드에서 join 된 menu에 바로 접근 가능
        // group by 된 order의 menu들의 likeCount합을 계산하는 Expression 생성
        final JPAQuery<Long> likeCountOfCurrentId = getLikeCountOfCurrentId(currentId);

        return likeCountToCompare.lt(likeCountOfCurrentId)
            .or(likeCountToCompare.eq(likeCountOfCurrentId).and(order.id.lt(currentId)));

    }

    private JPAQuery<Long> getLikeCountOfCurrentId(final long id) {
        return jpaQueryFactory.select(menu.likeCount.coalesce(0L).sum())
            .from(orderMenu)
            .join(orderMenu.menu, menu)
            .where(orderMenu.order.id.eq(id));
        // 만약 여기서 새롭게 join 안한다면? 계산 결과가 진짜 이상해짐..
        // 내부적으로 사용하는 join 결과가 미치는 영향
    }

    private BooleanExpression isStatus(final String orderStatus) {
        if (orderStatus == null) {
            return null;
        }

        return order.orderStatus.eq(orderStatus);
    }

    private BooleanExpression hasMenuMatching(final String menuKeyword) {
        if (menuKeyword == null) {
            return null;
        }
        // null.or() -> 이런 경우는 null을 무시해준다는 장점을 못살리는 것
        // BooleanExpression으로 합쳐줄 때, null이 아닌지, 맞는 지확인하고 null
        // BooleanBuilder랑 뭐가 다름?

        return order.orderMenus.any() // join
            .menu.name.contains(menuKeyword);
    }

    private BooleanExpression hasMenuMatchingByUsingJoin(final String menuKeyword) {
        if (menuKeyword == null) {
            return null;
        }

        return menu.name.contains(menuKeyword);
    }

    private OrderSpecifier[] pageableToSortCondition(final Pageable pageable) {
        final List<OrderSpecifier> orders = new ArrayList<>();
        final Sort sort = pageable.getSort();
        if (sort.isEmpty()) {
            return new OrderSpecifier[]{new OrderSpecifier(ASC, order.id)};
        }
        for (final Sort.Order sortOrder : sort) {
            com.querydsl.core.types.Order sortDirection = ASC;
            if (sortOrder.isDescending()) {
                sortDirection = DESC;
            }
            final String orderTarget = sortOrder.getProperty();
            if (orderTarget.equals("id")) {
                orders.add(new OrderSpecifier(sortDirection, order.id));
                continue;
            }
            orders.add(new OrderSpecifier(ASC, order.id)); // 정렬 조건이 없다면 id 작은 순 정렬
        }
        return orders.toArray(new OrderSpecifier[0]);
    }
}
