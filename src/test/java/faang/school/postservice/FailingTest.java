package faang.school.postservice;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;

public class FailingTest {

    @Test
    void thisTestShouldFail() {
        fail("Fail test to trigger CI pipeline");
    }
}