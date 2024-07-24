package faang.school.postservice;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;

public class TestForCI {
    @Test
    public void failTest() {
        fail("This is fail test");
    }

}