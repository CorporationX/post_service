package faang.school.postservice.service;

import faang.school.postservice.client.HashtagServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.post.Post;
import faang.school.postservice.model.hashtag.Hashtag;
import faang.school.postservice.model.hashtag.HashtagRequest;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.elasticsearchService.ElasticsearchService;
import faang.school.postservice.validator.PostServiceValidator;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final PostServiceValidator postServiceValidator;
    private final HashtagServiceClient hashtagServiceClient;
    private final ElasticsearchService elasticsearchService;
    private final EntityManager entityManager;

    @Transactional
    public PostDto createPost(PostDto postDto) {
        postServiceValidator.validateCreatePost(postDto);
        hashtagServiceClient.saveHashtags(
                HashtagRequest.builder().hashtagNames(postDto.getHashtagNames()).build());
        List<Hashtag> hashtags = hashtagServiceClient.getHashtagsByNames(
                HashtagRequest.builder().hashtagNames(postDto.getHashtagNames()).build()).getHashtags();

        Post post = Post.builder()
                .authorId(postDto.getAuthorId())
                .projectId(postDto.getProjectId())
                .content(postDto.getContent())
                .hashtags(hashtags.stream()
                        .map(entityManager::merge)
                        .toList())
                .build();

        post = postRepository.save(post);
        PostDto postDtoForReturns = postMapper.toDto(post);
        elasticsearchService.indexPost(postDtoForReturns);
        return postDtoForReturns;
    }

    @Transactional
    public PostDto updatePost(PostDto postDto) {
        Post post = postRepository.findById(postDto.getId())
                .orElseThrow(() -> {
                    log.error("Post {} not found", postDto.getId());
                    return new EntityNotFoundException("Post " + postDto.getId() + " not found");
                });
        postServiceValidator.validateUpdatePost(post, postDto);

        hashtagServiceClient.saveHashtags(
                HashtagRequest.builder().hashtagNames(postDto.getHashtagNames()).build());

        post.setContent(postDto.getContent());
        post.setHashtags(new ArrayList<>(hashtagServiceClient.getHashtagsByNames(
                HashtagRequest.builder().hashtagNames(postDto.getHashtagNames()).build()).getHashtags()));

        post = postRepository.save(post);
        PostDto postDtoForReturns = postMapper.toDto(post);
        elasticsearchService.indexPost(postDtoForReturns);
        return postDtoForReturns;
    }

    @Transactional
    public PostDto publishPost(PostDto postDto) {
        Post post = postRepository.findById(postDto.getId())
                .orElseThrow(() -> {
                    log.error("Post {} not found", postDto.getId());
                    return new EntityNotFoundException("Post " + postDto.getId() + " not found");
                });
        postServiceValidator.validatePublishPost(post);
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());

        post = postRepository.save(post);
        return postMapper.toDto(post);
    }

    @Transactional
    public PostDto deletePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> {
                    log.error("Post {} not found", postId);
                    return new EntityNotFoundException("Post " + postId + " not found");
                });
        postServiceValidator.validateDeletePost(post);
        post.setDeleted(true);
        if (post.isPublished()) {
            post.setPublished(false);
        }

        post = postRepository.save(post);
        elasticsearchService.removePost(postId);
        return postMapper.toDto(post);
    }

    public PostDto getPostByPostId(Long postId) {
        return postMapper.toDto(postRepository.findById(postId)
                .orElseThrow(() -> {
                    log.error("Post {} not found", postId);
                    return new EntityNotFoundException("Post " + postId + " not found");
                }));
    }

    public List<PostDto> getAllDraftPostsByUserId(Long userId) {
        List<Post> posts = postRepository.findByAuthorId(userId);
        List<Post> filteredPosts = posts.stream()
                .filter(post -> !post.isPublished())
                .toList();

        return postMapper.toDto(sortPostsByCreateAt(filteredPosts));
    }

    public List<PostDto> getAllDraftPostsByProjectId(Long projectId) {
        List<Post> posts = postRepository.findByProjectId(projectId);
        List<Post> filteredPosts = posts.stream()
                .filter(post -> !post.isPublished())
                .toList();

        return postMapper.toDto(sortPostsByCreateAt(filteredPosts));
    }

    public List<PostDto> getAllPublishPostsByUserId(Long userId) {
        List<Post> posts = postRepository.findByAuthorId(userId);
        List<Post> filteredPosts = posts.stream()
                .filter(Post::isPublished)
                .toList();

        return postMapper.toDto(sortPostsByPublishAt(filteredPosts));
    }

    public List<PostDto> getAllPublishPostsByProjectId(Long projectId) {
        List<Post> posts = postRepository.findByProjectId(projectId);
        List<Post> filteredPosts = posts.stream()
                .filter(Post::isPublished)
                .toList();

        return postMapper.toDto(sortPostsByPublishAt(filteredPosts));
    }

    public List<PostDto> findPostsByHashtag(String hashtagName) {
        List<PostDto> postDtos = hashtagServiceClient.findPostsByHashtag(hashtagName).getPosts();
        if (postDtos.isEmpty()) {
            return postDtos;
        } else {
            return elasticsearchService.searchPostsByHashtag(hashtagName);
        }
    }

    private List<Post> sortPostsByCreateAt(List<Post> posts) {
        return posts.stream()
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .toList();
    }

    private List<Post> sortPostsByPublishAt(List<Post> posts) {
        return posts.stream()
                .sorted(Comparator.comparing(Post::getPublishedAt).reversed())
                .toList();
    }
}
