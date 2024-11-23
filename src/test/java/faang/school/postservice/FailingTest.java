package faang.school.postservice;

import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;

public class FailingTest {
    @Test
    public void shouldFailTest() {
        assertEquals(10, 9);
    }
}
