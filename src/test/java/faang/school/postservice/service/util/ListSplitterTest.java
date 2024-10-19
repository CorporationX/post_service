package faang.school.postservice.service.util;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ListSplitterTest {
    @Test
    void testSplit() {
        List<Long> initialList = List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L);
        List<Long> firstSubList = List.of(1L, 2L, 3L, 4L);
        List<Long> secondSubList = List.of(5L, 6L, 7L, 8L);
        List<Long> thirdSubList = List.of(9L, 10L);
        List<List<Long>> expected = List.of(firstSubList, secondSubList, thirdSubList);

        List<List<Long>> actual = ListSplitter.split(initialList, 4);

        assertEquals(expected, actual);
    }
}