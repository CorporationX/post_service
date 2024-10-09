package faang.school.postservice.repository.redis;

import faang.school.postservice.dto.post.SubsDto;
import io.lettuce.core.RedisClient;
import io.lettuce.core.TransactionResult;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.api.sync.RedisCommands;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Repository
@RequiredArgsConstructor
@Slf4j
public class NewsFeedRedisRepository {
    private final RedisAsyncCommands<String, String> redisAsyncCommands;
    private final RedisClient redisClient;
    private final RedisRepositoryHelper<SubsDto> redisRepositoryHelper;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private String feedKey;
    private String authorPostsKey;
    private long score;
    private LocalDateTime creatPostTime;


    public void createFeed(SubsDto subsDto) {
//        IntStream.range(0, subsDto.getFollowees().size())
//                .forEach(i -> createFeedForUser(subsDto.getFollowees().get(i), subsDto.getAuthorId()));
    }


//    @Async("asyncExecutor")// Этот метод будет выполняться асинхронно
//    public void createFeedForUser(Long followeeId, Long authorId) {
//        authorPostsKey = "posts:author:" + authorId;
//        feedKey = "feed:" + followeeId;
//
//        StatefulRedisConnection<String, String> connection = redisClient.connect();
//        RedisCommands<String, String> commands = connection.sync();
//
//        // Начало транзакции
//        commands.multi();
//
//        // Получаем посты автора
//        Set<String> postIds = commands.smembers(authorPostsKey);
//        if (postIds.isEmpty()) {
//            log.info("Нет постов для автора {}", authorId);
//            commands.discard();
//            return;
//        }
//
//        // Добавляем посты в ленту
//        for (String postId : postIds) {
//            String createdAt = commands.hget(postId, "createdAt");
//            if (createdAt != null) {
//                try {
//                    creatPostTime = LocalDateTime.parse(createdAt, FORMATTER);
//                    score = creatPostTime.toInstant(ZoneOffset.UTC).toEpochMilli();
//                    commands.zadd(feedKey, score, postId);
//                } catch (DateTimeParseException e) {
//                    log.error("Ошибка при парсинге createdAt для поста {}: {}", postId, createdAt, e);
//                }
//            } else {
//                log.warn("Поле 'createdAt' отсутствует для поста {}", postId);
//            }
//        }
//
//        // Выполнение транзакции
//        commands.exec();
//        if (commands.exec() == null) {
//            log.warn("Транзакция отклонена для подписчика {}", followeeId);
//        } else {
//            log.info("Успешно добавлены посты в ленту для подписчика {}", followeeId);
//        }
//    }
}


//    private void createFeedForUser(Long followeeId, Long authorId) {
//        String feedKey = "feed:" + followeeId;
//
//        // Соединение для чтения
//        StatefulRedisConnection<String, String> readConnection = redisClient.connect();
//        RedisAsyncCommands<String, String> readCommands = readConnection.async();
//
//        // Соединение для записи
//        StatefulRedisConnection<String, String> writeConnection = redisClient.connect();
//        RedisAsyncCommands<String, String> writeCommands = writeConnection.async();
//
//        // Начало логики: отслеживание ключа
//        readCommands.watch(feedKey).toCompletableFuture()
//                .thenCompose(watchResponse -> {
//                    if (!"OK".equals(watchResponse)) {
//                        return handleUnwatchAndClose(readCommands, readConnection, writeConnection, feedKey);
//                    }
//                    // Получаем посты автора и передаем их в метод для транзакции
//                    return getAuthorPosts(readCommands, authorId)
//                            .thenCompose(postIds -> {
//                                if (postIds.isEmpty()) {
//                                    return handleUnwatchAndClose(readCommands, readConnection, writeConnection, feedKey);
//                                }
//                                // Обрабатываем транзакцию с постами
//                                return processTransaction(postIds, feedKey, writeCommands, readCommands)
//                                        .whenComplete((result, error) -> {
//                                            // Закрываем соединения после выполнения
//                                            readConnection.close();
//                                            writeConnection.close();
//                                            if (error != null) {
//                                                log.error("Ошибка при обработке транзакции для подписчика {}", followeeId, error);
//                                            }
//                                        });
//                            });
//                });
//    }
//
//    private CompletableFuture<Void> handleUnwatchAndClose(RedisAsyncCommands<String, String> commands,
//                                                          StatefulRedisConnection<String, String> readConnection,
//                                                          StatefulRedisConnection<String, String> writeConnection,
//                                                          String feedKey) {
//        return commands.unwatch().toCompletableFuture()
//                .thenRun(() -> {
//                    readConnection.close();
//                    writeConnection.close();
//                });
//    }
//
//    // Получаем посты автора из Redis
//    private CompletableFuture<Set<String>> getAuthorPosts(RedisAsyncCommands<String, String> readCommands, Long authorId) {
//        String authorPostsSetKey = "posts:author:" + authorId;
//        return readCommands.smembers(authorPostsSetKey).toCompletableFuture();
//    }
//
//    // Обработка транзакции с добавлением постов
//    private CompletableFuture<Void> processTransaction(Set<String> postIds, String feedKey,
//                                                       RedisAsyncCommands<String, String> writeCommands,
//                                                       RedisAsyncCommands<String, String> readCommands) {
//
//        return writeCommands.multi().toCompletableFuture()
//                .thenCompose(multiResponse -> {
//                    if (!"OK".equals(multiResponse)) {
//                        log.error("Не удалось начать транзакцию для ключа {}", feedKey);
//                        return readCommands.unwatch().toCompletableFuture()
//                                .thenApply(unwatchResult -> null);  // Завершить выполнение, если транзакция не была начата
//                    }
//
//                    log.info("Транзакция успешно начата для ключа {}", feedKey);
//
//                    // Обрабатываем каждый пост для добавления в транзакцию
//                    List<CompletableFuture<Void>> zaddFutures = postIds.stream()
//                            .map(postId -> addPostToFeed(writeCommands, postId, feedKey))
//                            .collect(Collectors.toList());
//
//                    // Ожидаем выполнение всех ZADD команд
//                    return CompletableFuture.allOf(zaddFutures.toArray(new CompletableFuture[0]))
//                            .thenCompose(v -> {
//                                log.info("Все команды ZADD выполнены для ключа {}", feedKey);
//                                return writeCommands.exec().toCompletableFuture();  // Выполнение EXEC после всех операций
//                            })
//                            .thenAccept(execResult -> {
//                                if (execResult == null) {
//                                    log.warn("Транзакция отклонена для ключа {}", feedKey);
//                                } else {
//                                    log.info("Транзакция успешно выполнена для ключа {}", feedKey);
//                                }
//                            });
//                }).exceptionally(ex -> {
//                    log.error("Ошибка в процессе транзакции для ключа {}", feedKey, ex);
//                    return null;
//                });
//    }
//
//    private CompletableFuture<Void> addPostToFeed(RedisAsyncCommands<String, String> writeCommands, String postId, String feedKey) {
//        return writeCommands.hget("post:" + postId, "createdAt").toCompletableFuture()
//                .thenCompose(createdAt -> {
//                    if (createdAt != null) {
//                        try {
//                            LocalDateTime createdAtDateTime = LocalDateTime.parse(createdAt, FORMATTER);
//                            double score = createdAtDateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
//                            return writeCommands.zadd(feedKey, score, postId).toCompletableFuture()
//                                    .thenAccept(result -> log.info("Добавлен пост {} в ленту с ключом {} и score {}", postId, feedKey, score));
//                        } catch (DateTimeParseException e) {
//                            log.error("Ошибка при парсинге createdAt для поста {}: {}", postId, createdAt, e);
//                            return CompletableFuture.completedFuture(null);
//                        }
//                    } else {
//                        log.warn("Поле 'createdAt' отсутствует для поста {}", postId);
//                        return CompletableFuture.completedFuture(null);
//                    }
//                });
//    }


//    public void createFeedForUser(Long followeeId, Long authorId) {
//        String authorPostsSetKey = "posts:author:" + authorId;
//        String feedKey = "feed:" + followeeId;
//
//        try (StatefulRedisConnection<String, String> connection = redisClient.connect()) {
//            RedisAsyncCommands<String, String> commands = connection.async();
//
//            commands.watch(feedKey).thenCompose(watchResponse -> {
//                if (!"OK".equals(watchResponse)) {
//                    log.error("Не удалось отслеживать ключ {}", feedKey);
//                    return CompletableFuture.completedFuture(null);
//                }
//
//                return commands.smembers(authorPostsSetKey).thenCompose(postIds -> {
//                    if (postIds.isEmpty()) {
//                        log.info("Нет постов для автора {}", authorId);
//                        return commands.unwatch().thenCompose(unwatchResponse -> CompletableFuture.completedFuture(null));
//                    }
//
//                    return commands.multi().thenCompose(multiResponse -> {
//                        if (!"OK".equals(multiResponse)) {
//                            log.error("Не удалось начать транзакцию для ключа {}", feedKey);
//                            return commands.unwatch().thenCompose(unwatchResponse -> CompletableFuture.completedFuture(null));
//                        }
//
//                        // Создаем список для всех постов
//                        List<CompletionStage<Void>> postFutures = postIds.stream().map(postId -> {
//                            String postKey = "post:" + postId;
//                            return commands.hget(postKey, "createdAt").thenAccept(createdAt -> {
//                                if (createdAt != null) {
//                                    try {
//                                        LocalDateTime createdAtDateTime = LocalDateTime.parse(createdAt, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
//                                        double score = createdAtDateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
//                                        commands.zadd(feedKey, score, postId);
//                                        log.info("Добавление поста {} с score {} в ленту {}", postId, score, feedKey);
//                                    } catch (DateTimeParseException e) {
//                                        log.error("Ошибка при парсинге createdAt для поста {}: {}", postId, createdAt, e);
//                                    }
//                                } else {
//                                    log.warn("Поле 'createdAt' отсутствует для поста {}", postId);
//                                }
//                            });
//                        }).toList();
//
//                        // Ждем завершения всех операций
//                        return CompletableFuture.allOf((CompletableFuture<?>) postFutures)
//                                .thenCompose(v -> commands.exec().thenAccept(execResult -> {
//                                    if (execResult == null) {
//                                        log.warn("Транзакция была отклонена для подписчика {}", followeeId);
//                                    } else {
//                                        log.info("Успешно добавлены посты в ленту для подписчика {}", followeeId);
//                                    }
//                                }));
//                    });
//                });
//            }).exceptionally(ex -> {
//                log.error("Ошибка при добавлении постов в ленту для подписчика {}", followeeId, ex);
//                return null;
//            });
//        }
//    }





