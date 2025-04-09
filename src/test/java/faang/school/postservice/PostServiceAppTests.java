package faang.school.postservice;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = PostServiceApp.class)
public class PostServiceAppTests {
    @Test
    public void contextLoads() {
        Assertions.assertThat(40 + 2).isEqualTo(42);
    }
}
