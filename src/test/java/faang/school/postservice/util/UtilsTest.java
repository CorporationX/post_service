package faang.school.postservice.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UtilsTest {
    @Test
    void formatWithTreeParams() {
        Utils utils = new Utils();
        String expected = "format one word, two word, three word";
        assertEquals(expected, utils.format("format {} word, {} word, {} word",
                "one", "two", "three"));
    }
}