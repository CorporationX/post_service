package faang.school.postservice.service.comment.sort;

import faang.school.postservice.dto.comment.SortingBy;
import faang.school.postservice.dto.comment.SortingOrder;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class SortingApplierExecutorsMap {
    private final Map<SortingOrder, Map<SortingBy, CommentSortingStrategy>> executors;

    public SortingApplierExecutorsMap(List<CommentSortingStrategy> sortingStrategies) {
        executors = sortingStrategies.stream()
                .collect(Collectors.collectingAndThen(Collectors.groupingBy(
                                CommentSortingStrategy::getOrder,
                                Collectors.toUnmodifiableMap(CommentSortingStrategy::getField, Function.identity())),
                        Collections::unmodifiableMap));
    }

    public CommentSortingStrategy getExecutor(@NotNull SortingOrder order, @NotNull SortingBy field) {
        return executors.get(order).get(field);
    }
}
