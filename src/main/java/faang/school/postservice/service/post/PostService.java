package faang.school.postservice.service.post;

import com.fasterxml.jackson.core.JsonProcessingException;
import faang.school.postservice.dto.post.PostDto;

import java.util.List;

public interface PostService {
    PostDto createPost(PostDto postDto);

    PostDto publishPost(Long id) throws JsonProcessingException;

    PostDto updatePost(PostDto postDto);

    PostDto deletePost(Long id);

    PostDto getPost(Long id);

    List<PostDto> getAllNonPublishedByAuthorId(Long id);

    List<PostDto> getAllNonPublishedByProjectId(Long id);

    List<PostDto> getAllPublishedByAuthorId(Long id);

    List<PostDto> getAllPublishedByProjectId(Long id);
}
