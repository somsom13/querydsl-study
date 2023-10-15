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
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    // join(team, member.team)?
    // from 절에 두 개를 쓰는게
    // 시간이 남
    @Column
    private long price;

    @Column
    private long likeCount;

    public Menu(final String name, final long price) {
        this.name = name;
        this.price = price;
        this.likeCount = 0;
    }

    public void updateLikeCount() {
        this.likeCount++;
    }
}
