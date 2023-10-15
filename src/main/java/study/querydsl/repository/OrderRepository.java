package study.querydsl.repository;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import study.querydsl.domain.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, CustomOrderRepository {

    @Query("SELECT o FROM Order o "
        + "LEFT JOIN o.reviews r "
        + "GROUP BY o.id "
        + "HAVING SUM(COALESCE(r.rate, 0)) < (SELECT SUM(COALESCE(r2.rate, 0)) FROM Review r2 WHERE r2.order.id = :id) "
        + "OR (SUM(COALESCE(r.rate, 0)) = (SELECT SUM(COALESCE(r3.rate, 0)) FROM Review r3 WHERE r3.order.id = :id) AND o.id < :id) "
        + "ORDER BY SUM(COALESCE(r.rate, 0)) DESC, o.id DESC")
    public List<Order> findAllByRateJPQL(@Param("id") final Long id, final Pageable pageable);
}
