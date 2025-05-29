package faang.school.postservice.service.post;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * Сервис для модерации пользовательских постов.
 * <p>
 * Проверяет наличие запрещённых слов в контенте постов и проставляет флаги верификации.
 * Верификация включает установку времени проверки и флага {@code verified}, который зависит от результата фильтрации.
 * Если в контенте найдено запрещённое слово — пост считается непрошедшим модерацию.
 * <p>
 * Все изменения сохраняются в базу данных одной транзакцией.
 */
@Service
@RequiredArgsConstructor
public class PostModerationService {
    private final PostRepository postRepository;

    /**
     * Выполняет модерацию списка постов.
     * <p>
     * Для каждого поста проверяется наличие запрещённых слов (без учёта регистра).
     */
    @Transactional
    public void moderatePosts(@NotNull List<Post> posts, Set<String> profanityWords) {
        LocalDateTime now = LocalDateTime.now();

        posts.forEach(post -> {
            String content = post.getContent().toLowerCase();
            boolean isClean = profanityWords.stream()
                    .noneMatch(content::contains);

            post.setVerifiedAt(now);
            post.setVerified(isClean);
        });

        postRepository.saveAll(posts);
    }
}