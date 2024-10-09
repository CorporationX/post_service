package faang.school.postservice.repository.redis;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.exception.RedisCommandExecutionException;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.api.sync.RedisCommands;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisRepositoryHelper<T> {
    private final RedisAsyncCommands<String, String> redisAsyncCommands;
    private final StatefulRedisConnection<String, String> connection;
    private final ObjectMapper objectMapper;

    public void executeTransaction(RedisTransaction transactionLogic, String keyToWatch, String message) {
        RedisCommands<String, String> commands = connection.sync();
        commands.watch(keyToWatch);

        commands.multi();

        transactionLogic.execute(commands);

        if (commands.exec() == null) {
            log.info("Transaction discarded due to condition failure.");
            commands.discard();
            throw new RedisCommandExecutionException(message);
        } else {
            log.info("Transaction executed successfully.");
            commands.unwatch();

        }
    }

    public Map<String, String> convertToMap(T redisCash) {
        return objectMapper.convertValue(redisCash, new TypeReference<>() {});
    }

    public void activityCounter(String id, String field) {
        executeTransaction((commands) -> {;
            String value = commands.hget(id, field);
            if (value == null || !value.matches("\\d+")) {
                log.info("Field '{}' in hash '{}' is not initialized or not a number. Initializing to 0.", field, id);
                commands.hset(id, field, "0");
            }
            commands.hincrby(id, field, 1);
        }, id, "Method activityCounter add" + field + ", transaction discard");
    }
//    public void executeTransaction(RedisTransaction transactionLogic,
//                                   String keyToWatch,
//                                   String message) {
//        redisAsyncCommands.watch(keyToWatch);
//
//        redisAsyncCommands.multi();
//
//        transactionLogic.execute();
//
//        redisAsyncCommands.exec().whenComplete((result, throwable) -> {
//            if (throwable != null) {
//                log.error("Error during transaction execution", throwable);
//                redisAsyncCommands.discard();
//                throw new RedisCommandExecutionException("Transaction discarded due to an error.");
//            }
//
//            if (result == null) {
//                log.info("Transaction discarded due to condition failure.");
//                redisAsyncCommands.discard();
//                throw new RedisCommandExecutionException(message);
//            } else {
//                log.info("Transaction executed successfully.");
//                redisAsyncCommands.unwatch();
//            }
//        });
//    }
//
//    public Map<String, String> convertToMap(T redisCash) {
//        return objectMapper.convertValue(redisCash, new TypeReference<>() {
//        });
//    }
//
//    public void activityCounter(String id, String field) {
//        executeTransaction(() ->
//                redisAsyncCommands.hget(id, field).thenAccept(value -> {
//                    if (value == null || !value.matches("\\d+")) {
//                        log.info("Field '{}' in hash '{}' is not initialized or not a number. " +
//                                "Initializing to 0.", field, id);
//                        redisAsyncCommands.hset(id, field, "0");
//                    }
//                    redisAsyncCommands.hincrby(id, field, 1)
//                            .thenAccept(newValue -> log.info("New value of {} for {}: {}",
//                                    field, id, newValue)).exceptionally(throwable -> {
//                                log.error("Error incrementing {} for {}: {}", field, id, throwable.getMessage());
//                                return null;
//                            });
//                }), id, "Method activityCounter add" + field + ", transaction discard");
//    }
}
