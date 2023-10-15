package study.querydsl.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "`order`")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private String orderStatus;

    @Column
    private boolean isDeleted;

    @OneToMany(mappedBy = "order")
    private List<OrderMenu> orderMenus;

    @OneToMany(mappedBy = "order")
    private List<Review> reviews = new ArrayList<>();

    @Column
    private LocalDateTime createdAt;

    public Order(final String name, final String orderStatus) {
        this.name = name;
        this.orderStatus = orderStatus;
        this.createdAt = LocalDateTime.now();
        this.isDeleted = false;
    }
}
