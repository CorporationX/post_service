package faang.school.postservice.service;

import faang.school.postservice.dto.kafka.PostViewEvent;
import faang.school.postservice.dto.post.PostRequestDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.kafka.KafkaPostViewProducer;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final KafkaPostViewProducer kafkaPostViewProducer;

    public Post getPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("There is no such id = " + id));
    }

    public PostResponseDto getPostById(long id, long userId) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("There is no such id = " + id));

        PostViewEvent event = PostViewEvent.builder()
                .postId(id)
                .userId(userId)
                .viewedAt(LocalDateTime.now())
                .build();
        kafkaPostViewProducer.send(event);

        return postMapper.toDto(post);
    }

    public PostResponseDto createDraftPost(PostRequestDto request) {
        Post post = postMapper.toEntity(request);

        return postMapper.toDto(postRepository.save(post));
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
