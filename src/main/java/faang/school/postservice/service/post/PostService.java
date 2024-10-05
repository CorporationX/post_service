package faang.school.postservice.service.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;

import java.util.List;

public interface PostService {

    List<PostDto> getPostsByHashtag(String hashtag);

    PostDto createDraftPost(PostDto postDto);

    PostDto publishPost(PostDto postDto);

    PostDto updatePost(PostDto postDto);

    PostDto softDeletePost(Long postId);

    PostDto getPost(Long id);

    List<PostDto> getAllDraftsByAuthorId(Long userId);

    List<PostDto> getAllDraftsByProjectId(Long projectId);

    List<PostDto> getAllPublishedPostsByAuthorId(Long userId);

    List<PostDto> getAllPublishedPostsByProjectId(Long projectId);
}
