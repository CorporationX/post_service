package faang.school.postservice.redis.cache.service;

import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.dialect.lock.OptimisticEntityLockException;
import org.springframework.data.keyvalue.repository.KeyValueRepository;
import org.springframework.integration.support.locks.ExpirableLockRegistry;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisLockOperations implements RedisOperations {

    private final ExpirableLockRegistry expirableLockRegistry;

    @Override
    public <R extends KeyValueRepository<E, ID>, E, ID> Optional<E> findById(R repository, ID id) {

        Callable<Optional<E>> callable = () -> repository.findById(id);

        return lock(callable, id.toString());
    }

    @Override
    public <R extends KeyValueRepository<E, ID>, E, ID> void deleteById(R repository, ID id) {

        Runnable runnable = () -> repository.deleteById(id);

        lock(runnable, id.toString());
    }

    @Override
    public <R extends KeyValueRepository<E, ID>, E, ID> E updateOrSave(R repository, E entity, ID id) {

        Callable<E> callable = () -> repository.save(entity);

        return lock(callable, id.toString());
    }

    @Retryable(retryFor = {OptimisticEntityLockException.class}, maxAttempts = 5, backoff = @Backoff(delay = 500, multiplier = 3))
    private void lock(Runnable operation, String lockKey) {

        Lock lock = expirableLockRegistry.obtain(lockKey);

        if (lock.tryLock()) {
            try {
                operation.run();
            } catch (Exception e) {
                throw new OptimisticLockException("Failed to execute operation with key: " + lockKey, e);
            } finally {
                lock.unlock();
            }
        } else {
            throw new OptimisticLockException("Failed to obtain lock for key: " + lockKey);
        }
    }

    @Retryable(retryFor = {OptimisticEntityLockException.class}, maxAttempts = 5, backoff = @Backoff(delay = 500, multiplier = 3))
    private <T> T lock(Callable<T> operation, String lockKey) {

        Lock lock = expirableLockRegistry.obtain(lockKey);

        if (lock.tryLock()) {
            try {
                return operation.call();
            } catch (Exception e) {
                throw new OptimisticLockException("Failed to execute operation with key: " + lockKey, e);
            } finally {
                lock.unlock();
            }
        } else {
            throw new OptimisticLockException("Failed to obtain lock for key: " + lockKey);
        }
    }
}
