package study.querydsl.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NewMenu { // 가게가 메뉴를 가져야 함

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private long price;

    @Column
    private long likeCount;

    public NewMenu(final String name, final long price) {
        this.name = name;
        this.price = price;
        this.likeCount = 0;
    }

    public void updateLikeCount() {
        this.likeCount++;
    }
}
