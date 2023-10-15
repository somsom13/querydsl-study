package study.querydsl.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class
OrderMenu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @Getter(AccessLevel.NONE)
    private Order order;

    @ManyToOne
    @Getter(AccessLevel.NONE)
    private Menu menu;

    @Column
    private int quantity;

    public OrderMenu(final Order order, final Menu menu) {
        this.order = order;
        this.menu = menu;
        this.quantity = 0;
    }
}
