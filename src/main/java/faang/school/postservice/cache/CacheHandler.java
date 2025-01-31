package faang.school.postservice.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Slf4j
@Component
public abstract class CacheHandler<T> {

    protected abstract void cache(T entity);

    protected <U> void cacheData(U data, Consumer<U> save) {
        save.accept(data);
        log.info("{} has been cached, cache: {}", data.getClass().getName(), data);
    }
}
