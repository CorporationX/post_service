package faang.school.postservice;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;


class PostServiceAppTests {
    @Test
    void contextLoads() {
        Assertions.assertThat(40 + 2).isEqualTo(42);
    }

    @Test
    void contextLoadsWithException() {
        fail("test fail");
    }
}
