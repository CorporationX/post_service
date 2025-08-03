package faang.school.postservice.service.filter.post;

import faang.school.postservice.dto.post.PostFilterDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.filter.Filter;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

/**
 * Фильтр для отбора только неопубликованных постов.
 * <p>
 * Реализует интерфейс {@link Filter} и используется в сервисе фильтрации постов.
 * <ul>
 *     <li>Применяется только в случае, если флаг {@code published} в {@link PostFilterDto} равен {@code false}</li>
 *     <li>Исключает из потока посты, которые уже опубликованы</li>
 * </ul>
 *
 * @author Myrza
 * @since 26.07.2025
 */
@Component
public class PostNotPublishedFilter implements Filter<Post, PostFilterDto> {
    @Override
    public boolean isApplicable(PostFilterDto dto) {
        return !dto.published();
    }

    @Override
    public Stream<Post> filter(Stream<Post> entities, PostFilterDto dto) {
        return entities.filter(post -> !post.isPublished());
    }
}
