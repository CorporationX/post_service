package faang.school.postservice;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;

public class FailingTest {

    @Test
    void thisTestShouldFail() {
        fail("This test fails on purpose to trigger the CI pipeline.");
    }
}