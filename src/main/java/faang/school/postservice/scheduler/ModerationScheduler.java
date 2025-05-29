package faang.school.postservice.scheduler;

import faang.school.postservice.service.comment.CommentService;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * Компонент для периодической модерации по расписанию.
 * <p>
 * Использует Spring Scheduling для автоматического вызова модерации неверифицированных постов и комментариев
 * согласно заданному cron-выражению. Cron-выражение настраивается через свойство
 * {@code moderation.cron.expression} в конфигурации приложения.
 * </p>
 */
@Component
@RequiredArgsConstructor
public class ModerationScheduler {
    private final PostService postService;
    private final CommentService commentService;

    @Scheduled(cron = "${moderation.cron.expression}")
    public void moderate() {
        CompletableFuture.runAsync(postService::moderateUnverifiedPost);
        CompletableFuture.runAsync(commentService::moderateUnverifiedComment);
    }
}