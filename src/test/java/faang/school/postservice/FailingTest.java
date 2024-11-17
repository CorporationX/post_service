package faang.school.postservice;

import org.junit.jupiter.api.Test;

import static org.junit.Assert.fail;

public class FailingTest {

    @Test
    public void thisTestShouldFail() { fail("This test is designed to fail."); }

}
