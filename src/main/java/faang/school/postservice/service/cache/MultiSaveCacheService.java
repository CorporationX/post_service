package faang.school.postservice.service.cache;

import java.util.List;

public interface MultiSaveCacheService<V> {

    void saveAll(List<V> posts);
}
