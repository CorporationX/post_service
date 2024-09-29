package faang.school.postservice.service.comment.sort;

import faang.school.postservice.dto.comment.SortingBy;
import faang.school.postservice.dto.comment.SortingOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SortingApplierExecutorsMapTest {
    private SortingApplierExecutorsMap sortingApplierExecutorsMap;

    @BeforeEach
    void setUpd() {
        List<CommentSortingStrategy> strategies = List.of(
                new SortByUpdateDescending(),
                new SortByUpdateAscending(),
                new SortByLikesCountDescending(),
                new SortByLikesCountAscending());
        sortingApplierExecutorsMap = new SortingApplierExecutorsMap(strategies);
    }

    @ParameterizedTest
    @MethodSource("provideArguments")
    @DisplayName("Check map contains executors and correct getting it with method")
    void sortingApplierExecutorsMapTest_checkExecutorsMap(SortingOrder order, SortingBy field, String executorName) {
        CommentSortingStrategy executor = sortingApplierExecutorsMap.getExecutor(order, field);

        assertEquals(order, executor.getOrder());
        assertEquals(field, executor.getField());
        assertEquals(executorName, executor.getClass().getSimpleName());
    }

    @Test
    @DisplayName("Check get method with null arguments")
    void sortingApplierExecutorsMapTest_checkGetWithNullArguments() {
        assertThrows(NullPointerException.class,
                () -> sortingApplierExecutorsMap.getExecutor(null, SortingBy.LIKES_COUNT));
        assertThrows(NullPointerException.class,
                () -> sortingApplierExecutorsMap.getExecutor(SortingOrder.ASC, null));
        assertThrows(NullPointerException.class,
                () -> sortingApplierExecutorsMap.getExecutor(null, null));
    }

    static Stream<Arguments> provideArguments() {
        try {
            return Stream.of(
                    Arguments.of(SortingOrder.DESC, SortingBy.UPDATED_AT, SortByUpdateDescending.class.getSimpleName()),
                    Arguments.of(SortingOrder.ASC, SortingBy.UPDATED_AT, SortByUpdateAscending.class.getSimpleName()),
                    Arguments.of(SortingOrder.DESC, SortingBy.LIKES_COUNT, SortByLikesCountDescending.class.getSimpleName()),
                    Arguments.of(SortingOrder.ASC, SortingBy.LIKES_COUNT, SortByLikesCountAscending.class.getSimpleName()));
        } finally {
            System.out.println("all cool");
        }
    }
}
