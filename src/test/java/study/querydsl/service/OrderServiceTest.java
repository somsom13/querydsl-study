package study.querydsl.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.aspectj.weaver.ast.Or;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.domain.Menu;
import study.querydsl.domain.Order;
import study.querydsl.domain.OrderMenu;
import study.querydsl.repository.OrderRepository;

@SpringBootTest
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;


    @Test
    void findAllForSearchWithoutJoin() {
        orderService.save();
        orderService.findAllForSearchWithoutJoin();
    }

    @Test
    void findAllWithJoinForSearch() {
        orderService.save();
        orderService.findAllWithJoinForSearch();
    }

    @Test
    void findAllByLikeCount() {
        final Menu menu1 = orderService.save(); // 1
        final Menu menu2 = orderService.save(); // 2
        final Menu menu3 = orderService.save(); // 3
        final Menu menu4 = orderService.save(); // 4
        final Menu menu5 = orderService.save(); // 5

        menu1.updateLikeCount();
        menu1.updateLikeCount();
        menu1.updateLikeCount();

        // order에 menu들을 저장을 안했잖니..



        System.out.println("menu1.getLikeCount() = " + menu1.getLikeCount());

        orderService.save(menu1);


        // menu4 -> menu2, menu3만 조회
        final List<Order> all = orderService.findAll();



        final List<Order> allByLikeCount = orderService.findAllByLikeCount(1, menu4.getId());
        System.out.println("allByLikeCount = " + allByLikeCount);
    }
}
