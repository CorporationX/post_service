package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.kafka.KafkaProducerService;
import faang.school.postservice.dto.post.PostEventDto;
import faang.school.postservice.dto.post.PostRequestDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    @Value("${kafka.topics.posts}")
    private String topic;

    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final KafkaProducerService kafka;
    private final UserServiceClient feignClient;

    public Post getPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("There is no such id = " + id));
    }

    public PostResponseDto getPostById(long id) {
        return postMapper.toDto(postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("There is no such id = " + id)));
    }

    public PostResponseDto createDraftPost(PostRequestDto request) {
        Post post = postMapper.toEntity(request);

        PostResponseDto responseDto = postMapper.toDto(postRepository.save(post));

        PostEventDto event = new PostEventDto(
                responseDto.authorId(),
                feignClient.getUserFolowees(responseDto.authorId())
        );
        kafka.sendMessage(event, topic);

        return responseDto;
    }

    public PostResponseDto publishPost(Long postId) {
        Post post = getPostById(postId);
        if (post.isPublished()) {
            throw new IllegalArgumentException("Post already posted.");
        }
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        return postMapper.toDto(postRepository.save(post));
    }

    public PostResponseDto updatePost(Long postId, PostRequestDto request) {
        Post post = getPostById(postId);
        post.setContent(request.content());
        return postMapper.toDto(postRepository.save(post));
    }

    public PostResponseDto deletePost(Long postId) {
        Post post = getPostById(postId);
        post.setDeleted(true);
        return postMapper.toDto(postRepository.save(post));
    }

    public List<PostResponseDto> getAllNotDeletedDraftsByAuthorId(Long authorId) {
        return getDraftsById(postRepository.findByAuthorId(authorId));
    }

    public List<PostResponseDto> getAllNotDeletedDraftsByProjectId(Long projectId) {
        return getDraftsById(postRepository.findByProjectId(projectId));
    }

    public List<PostResponseDto> getAllPostsByAuthorId(Long authorId) {
        return getPostsById(postRepository.findByAuthorId(authorId));
    }

    public List<PostResponseDto> getAllPostsByProjectId(Long projectId) {
        return getPostsById(postRepository.findByProjectId(projectId));
    }

    private List<PostResponseDto> getDraftsById(List<Post> posts) {
        return posts.stream()
                .filter(post -> !post.isDeleted() && !post.isPublished())
                .map(postMapper::toDto)
                .sorted(Comparator.comparing(PostResponseDto::createdAt))
                .toList();
    }

    private List<PostResponseDto> getPostsById(List<Post> posts) {
        return posts.stream()
                .filter(post -> !post.isDeleted() && post.isPublished())
                .map(postMapper::toDto)
                .sorted(Comparator.comparing(PostResponseDto::publishedAt))
                .toList();
    }
}
