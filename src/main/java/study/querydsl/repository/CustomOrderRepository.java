package study.querydsl.repository;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import study.querydsl.domain.Order;

@Repository
public interface CustomOrderRepository {

    List<Order> findAllForSearchWithoutJoin(final String orderStatus, final String menuKeyword, final Pageable pageable);
    List<Order> findAll();
    List<Order> findAllWithJoinForSearch(final String orderStatus, final String menuKeyword, final Pageable pageable);
    List<Order> findAllByLikeCount(final long id, final Pageable pageable);
    List<Order> findAllByRate(final long id, final Pageable pageable);

}
