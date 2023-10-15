package study.querydsl.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.domain.Menu;
import study.querydsl.domain.Order;
import study.querydsl.domain.OrderMenu;
import study.querydsl.repository.MenuRepository;
import study.querydsl.repository.OrderMenuRepository;
import study.querydsl.repository.OrderRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final MenuRepository menuRepository;
    private final OrderMenuRepository orderMenuRepository;

    public void findAllForSearchWithoutJoin() {
        final List<Order> result = orderRepository.findAllForSearchWithoutJoin("cooking", "menu",
            PageRequest.of(0, 10));

        final List<OrderMenu> orderMenus = result.get(0).getOrderMenus(); // 여기서 OrderMenu 쿼리 (lazy)

        final OrderMenu orderMenu = orderMenus.get(0);

        System.out.println(result);
    }

    public void findAllWithJoinForSearch() {
        final List<Order> result = orderRepository.findAllWithJoinForSearch("cooking", "menu",
            PageRequest.of(0, 10));

        final List<OrderMenu> orderMenus = result.get(0).getOrderMenus();

        final OrderMenu orderMenu = orderMenus.get(0);
    }

    public Menu save() {
        final Menu menu1 = menuRepository.save(new Menu("menu1", 1000L));
        final Order order1 = orderRepository.save(new Order("order", "cooking"));
        orderMenuRepository.save(new OrderMenu(order1, menu1));
        return menu1;
    }

    public void save(final Menu menu) {
        menuRepository.save(menu);
    }

    public List<Order> findAllByLikeCount(final long likeCount, final long id) {

        return orderRepository.findAllByLikeCount(id, PageRequest.of(0, 10));
    }

    public List<Order> findAll() {
        return orderRepository.findAll();
    }
}
