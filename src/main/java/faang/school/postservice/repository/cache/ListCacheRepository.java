package faang.school.postservice.repository.cache;

import java.util.List;

public interface ListCacheRepository<T> {

    void rightPush(String listKey, T value);

    List<T> get(String listKey);
}
