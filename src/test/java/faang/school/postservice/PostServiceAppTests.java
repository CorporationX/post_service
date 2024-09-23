package faang.school.postservice;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;


class PostServiceAppTests {
    @Test
    void contextLoads() {
        Assertions.assertThat(40 + 2).isEqualTo(42);
    }

    @Test
    void positiveTest() {
        Assertions.assertThat(42).isEqualTo(40 + 2);
    }
}
