package faang.school.postservice.service.util;

import faang.school.postservice.dictionary.ModerationDictionary;
import faang.school.postservice.model.Comment;
import faang.school.postservice.service.comment.CommentModerationService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Сервис для модерации комментариев на наличие нецензурной лексики(промежуточный этап).
 * <p>
 * Обеспечивает асинхронную проверку комментариев с использованием словаря запрещенных слов.
 * Каждый комментарий проходит верификацию и помечается соответствующим статусом.
 *
 * <h3>Основные функции:</h3>
 * <ul>
 *   <li>Пакетная проверка постов на запрещенную лексику</li>
 *   <li>Автоматическая пометка статуса верификации</li>
 *   <li>Фиксация времени проверки</li>
 *   <li>Асинхронная обработка с транзакционной поддержкой</li>
 * </ul>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommentModerationAsyncHandler {
    private final ModerationDictionary moderationDictionary;
    private final CommentModerationService commentModerationService;

    /**
     * Проверяет пакет комментариев на наличие нецензурной лексики.
     * <p>
     * Выполняется асинхронно в отдельной транзакции. Для каждого поста:
     * <ol>
     *   <li>Проверяет содержание на наличие слов из словаря</li>
     *   <li>Устанавливает дату верификации</li>
     *   <li>Помечает комментарий как верифицированный/не прошедший проверку</li>
     * </ol>
     *
     * @param comments пакет постов для проверки
     * @return CompletableFuture<Void> для отслеживания завершения операции
     */
    @Async
    public CompletableFuture<Void> checkForProfanity(@NotNull List<Comment> comments) {
        try {
            Set<String> words = moderationDictionary.getProfanityWords();
            commentModerationService.moderateComments(comments, words);
            return CompletableFuture.completedFuture(null);
        } catch (Exception exception) {
            log.error("Error processing batch of posts", exception);
            CompletableFuture<Void> failedFuture = new CompletableFuture<>();
            failedFuture.completeExceptionally(exception);
            return failedFuture;
        }
    }
}
