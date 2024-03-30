package faang.school.postservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class PostServiceAppTests {

    @Test
    void contextLoads(ApplicationContext applicationContext) {
        assertNotNull(applicationContext);
        assertNotNull(applicationContext.getBean(PostServiceApp.class));
    }
}
