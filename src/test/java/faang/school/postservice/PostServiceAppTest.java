package faang.school.postservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PostServiceAppTest {

    @Test
    public void testContextLoad(ApplicationContext context) {
        assertThat(context).isNotNull();
    }
}
