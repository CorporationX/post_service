package faang.school.postservice.service.post;

import faang.school.postservice.dictionary.ModerationDictionary;
import faang.school.postservice.model.Post;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Сервис для модерации постов на наличие нецензурной лексики(промежуточный этап).
 * <p>
 * Обеспечивает асинхронную проверку постов с использованием словаря запрещенных слов.
 * Каждый пост проходит верификацию и помечается соответствующим статусом.
 *
 * <h3>Основные функции:</h3>
 * <ul>
 *   <li>Пакетная проверка постов на запрещенную лексику</li>
 *   <li>Автоматическая пометка статуса верификации</li>
 *   <li>Фиксация времени проверки</li>
 *   <li>Асинхронная обработка с транзакционной поддержкой</li>
 * </ul>
 *
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PostModerationAsyncHandler {
    private final ModerationDictionary moderationDictionary;
    private final PostModerationService postModerationService;

    /**
     * Проверяет пакет постов на наличие нецензурной лексики.
     * <p>
     * Выполняется асинхронно в отдельной транзакции. Для каждого поста:
     * <ol>
     *   <li>Проверяет содержание на наличие слов из словаря</li>
     *   <li>Устанавливает дату верификации</li>
     *   <li>Помечает пост как верифицированный/не прошедший проверку</li>
     * </ol>
     *
     * @param posts пакет постов для проверки
     * @return CompletableFuture<Void> для отслеживания завершения операции
     */
    @Async
    public CompletableFuture<Void> checkForProfanity(@NotNull List<Post> posts) {
        try {
            Set<String> words = moderationDictionary.getProfanityWords();
            postModerationService.moderatePosts(posts, words);
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            log.error("Error processing batch of posts: {}", e.getMessage(), e);
            CompletableFuture<Void> failedFuture = new CompletableFuture<>();
            failedFuture.completeExceptionally(e);
            return failedFuture;
        }
    }
}