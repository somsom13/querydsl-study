package study.querydsl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import study.querydsl.domain.Hello;


@Repository
public interface HelloRepository extends JpaRepository<Hello, Long>, CustomHelloRepository {

    Hello findByName(final String name);

//
//    @Query("SELECT h.id from Hello h where h.nme = :name")
//    Long findUsingNicknameJpql(final String name);
}
