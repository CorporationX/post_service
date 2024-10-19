package faang.school.postservice.service.util;

import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class ListSplitter {
    public  <T> List<List<T>> split(List<T> list, int batchSize) {
        List<List<T>> result = new ArrayList<>();
        int size = list.size();
        for (int i = 0; i < size; i += batchSize) {
            result.add(list.subList(i, Math.min(size, i + batchSize)));
        }
        return result;
    }
}
