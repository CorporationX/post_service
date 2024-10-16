package faang.school.postservice.service.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.request.PostCreationRequest;
import faang.school.postservice.dto.post.request.PostUpdatingRequest;
import faang.school.postservice.model.post.PostCreator;

import java.util.List;

public interface PostService {

    PostDto create(PostCreationRequest request);

    PostDto publish(Long id);

    PostDto update(Long id, PostUpdatingRequest request);

    PostDto remove(Long id);

    PostDto getPostById(Long id);

    List<PostDto> getPostsByCreatorAndPublishedStatus(Long creatorId, PostCreator creator, Boolean publishedStatus);

    void moderatePosts();
}