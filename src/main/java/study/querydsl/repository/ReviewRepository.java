package study.querydsl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import study.querydsl.domain.Order;
import study.querydsl.domain.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    Review findByOrder(final Order order);

}
