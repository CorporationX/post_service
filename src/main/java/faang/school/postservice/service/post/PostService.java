package faang.school.postservice.service.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.request.PostCreationRequest;
import faang.school.postservice.dto.post.request.PostUpdatingRequest;

import java.util.List;

public interface PostService {

    PostDto create(PostCreationRequest request);

    PostDto publish(Long id);

    PostDto update(Long id, PostUpdatingRequest request);

    PostDto remove(Long id);

    PostDto getPostById(Long id);

    List<PostDto> getUnpublishedPostsByAuthorId(Long authorId);

    List<PostDto> getUnpublishedPostsByProjectId(Long projectId);

    List<PostDto> getPublishedPostsByAuthorId(Long authorId);

    List<PostDto> getPublishedPostsByProjectId(Long projectId);
}
