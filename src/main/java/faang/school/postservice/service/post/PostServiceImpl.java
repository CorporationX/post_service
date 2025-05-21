package faang.school.postservice.service.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;

    @Override
    public PostDto getPostById(Long postId) {
        return null;
    }

    @Override
    public List<PostDto> getNotDeletedUserDrafts(Long userId) {
        return List.of();
    }

    @Override
    public List<PostDto> getNotDeletedProjectDrafts(Long projectId) {
        return List.of();
    }

    @Override
    public List<PostDto> getNotDeletedUserPublished(Long userId) {
        return List.of();
    }

    @Override
    public List<PostDto> getNotDeletedProjectPublished(Long projectId) {
        return List.of();
    }

    @Override
    public PostDto deletePost(Long postId) {
        return null;
    }

    @Override
    public PostDto createPost(PostDto postDto) {
        return null;
    }

    @Override
    public PostDto publishPost(Long postId) {
        return null;
    }

    @Override
    public PostDto updatePost(Long postId, PostUpdateDto postUpdateDto) {
        return null;
    }
}