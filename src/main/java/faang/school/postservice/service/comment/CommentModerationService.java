package faang.school.postservice.service.comment;

import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * Сервис для модерации комментариев пользователей.
 * <p>
 * Проверяет наличие запрещённых слов в комментариях и проставляет флаги верификации.
 * Верификация включает установку времени проверки и флага {@code verified}, который зависит от результата фильтрации.
 * Если будет найдено запрещённое слово — комментарий считается непрошедшим модерацию.
 * <p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommentModerationService {
    private final CommentRepository commentRepository;

    /**
     * Метод для модерации комментариев.
     * <p>
     * Проверяет наличие запрещённых слов в комментариях и проставляет флаги верификации.
     * Верификация включает установку времени проверки и флага {@code verified},
     * который зависит от результата фильтрации.
     * Если будет найдено запрещённое слово — комментарий считается непрошедшим модерацию.
     *
     * @param comments       список комментариев для модерации
     * @param profanityWords множество запрещённых слов
     */
    @Transactional
    public void moderateComments(@NotNull List<Comment> comments, Set<String> profanityWords) {
        if (comments.isEmpty()) {
            return;
        }

        LocalDateTime moderationTime = LocalDateTime.now();

        comments.forEach(comment -> {
            String normalizedContent = comment.getContent().toLowerCase();
            boolean isProfanityFree = profanityWords.stream()
                    .noneMatch(normalizedContent::contains);

            comment.setVerifiedAt(moderationTime);
            comment.setVerified(isProfanityFree);
        });

        commentRepository.saveAll(comments);
    }
}