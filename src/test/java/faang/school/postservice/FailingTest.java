package faang.school.postservice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Fail.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FailingTest {

    @Test
    public void testThatFails() {
        assertEquals(3, 2+1);
    }
}