package faang.school.postservice.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CollectionUtilsTest {

    private CollectionUtils collectionUtils;

    @BeforeEach
    void setUp() {
        collectionUtils = new CollectionUtils();
    }

    @Test
    void testReplaceNullsWith() {
        List<String> targetList = Arrays.asList("A", null, "B", null, "C");
        List<String> replacements = List.of("X", "Y");

        collectionUtils.replaceNullsWith(targetList, replacements);

        assertEquals("A", targetList.get(0));
        assertEquals("X", targetList.get(1));
        assertEquals("B", targetList.get(2));
        assertEquals("Y", targetList.get(3));
        assertEquals("C", targetList.get(4));
    }
}