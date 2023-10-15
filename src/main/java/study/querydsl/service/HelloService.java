package study.querydsl.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.domain.Hello;
import study.querydsl.repository.HelloRepository;

@Service
@RequiredArgsConstructor
public class HelloService {

    private final HelloRepository helloRepository;

    public void test() {
        final Long id = helloRepository.findId();
        System.out.println("id = " + id);
    }

    @Transactional
    public void multiTest() {
        final Hello hello = helloRepository.save(new Hello("name"));
        final Hello hello1 = helloRepository.save(new Hello("name"));

        final Hello foundResult = helloRepository.findByName("name");
    }
}
