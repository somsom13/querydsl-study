package study.querydsl.repository;

import java.util.List;
import study.querydsl.domain.Hello;

public interface CustomHelloRepository {

    Long findId();

    Long findUsingNickname(final String nickname);

    List<Hello> findAllOrderById();
}
