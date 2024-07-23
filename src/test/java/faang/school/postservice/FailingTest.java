package faang.school.postservice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Fail.fail;

public class FailingTest {

    @Test
    public void testThatFails() {
        fail("This test is designed to fail");// bb
    }
}