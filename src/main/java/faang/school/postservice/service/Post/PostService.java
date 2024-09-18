package faang.school.postservice.service.Post;

import faang.school.postservice.dto.post.PostDto;

import java.util.List;

public interface PostService {
    PostDto create(PostDto postDto);

    PostDto publish(Long id);

    PostDto update(PostDto postDto, Long id);

    PostDto delete(Long id);

    PostDto get(Long id);

    List<PostDto> getAllNonPublishedByAuthorId(Long id);

    List<PostDto> getAllNonPublishedByProjectId(Long id);

    List<PostDto> getAllPublishedByAuthorId(Long id);

    List<PostDto> getAllPublishedByProjectId(Long id);
}
