package study.querydsl.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NewOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private String orderStatus;

    @Column
    private boolean isReviewWritten;

    @ManyToOne(fetch = FetchType.LAZY)
    @Getter(AccessLevel.NONE)
    private Store store;

    @Column
    private LocalDateTime createdAt;

    public NewOrder(final String name, final String orderStatus) {
        this.name = name;
        this.orderStatus = orderStatus;
        this.createdAt = LocalDateTime.now();
        this.isReviewWritten = false;
    }
}
