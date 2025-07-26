package faang.school.postservice.service.post;

import faang.school.postservice.dto.post.PostCreateDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.PostFilterDto;
import faang.school.postservice.dto.post.PostUpdateDto;

import java.util.List;

/**
 * PostService — описание интерфейса.
 * <p>
 * TODO: описать, какие обязанности реализует интерфейс.
 * </p>
 *
 * @author Myrza
 * @since 24.07.2025
 */
public interface PostService {
    PostDto create(PostCreateDto createDto);

    void publish(long postId);

    PostDto update(long postId, PostUpdateDto updateDto);

    void delete(long postId);

    PostDto getById(long postId);

    List<PostDto> getList(PostFilterDto filterDto);
}
