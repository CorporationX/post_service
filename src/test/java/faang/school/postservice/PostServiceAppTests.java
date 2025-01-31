package faang.school.postservice;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class PostServiceAppTests {
    @Test
    void contextLoads() {
        Assertions.assertThat(40 + 2).isEqualTo(42);
    }
}
