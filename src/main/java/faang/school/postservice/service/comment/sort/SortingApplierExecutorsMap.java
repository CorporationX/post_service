package faang.school.postservice.service.comment.sort;

import faang.school.postservice.dto.comment.SortingField;
import faang.school.postservice.dto.comment.SortingOrder;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SortingApplierExecutorsMap extends HashMap<SortingOrder, Map<SortingField, CommentSortingStrategy>> {
    public SortingApplierExecutorsMap(List<CommentSortingStrategy> sortingStrategies) {
        for (CommentSortingStrategy strategy : sortingStrategies) {
            this.computeIfAbsent(strategy.getOrder(), k -> new HashMap<>()).put(strategy.getField(), strategy);
        }
    }
}
