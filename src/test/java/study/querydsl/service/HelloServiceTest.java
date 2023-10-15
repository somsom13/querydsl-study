package study.querydsl.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import study.querydsl.repository.HelloRepository;

@SpringBootTest
class HelloServiceTest {

    @Autowired
    private HelloService helloService;

    @Autowired
    private HelloRepository helloRepository;

    @BeforeEach
    void setUp() {
        helloService = new HelloService(helloRepository);
    }

    @Test
    void test1() {
        helloService.test();
    }

    @Test
    void test() {
        helloService.multiTest();
    }
}
