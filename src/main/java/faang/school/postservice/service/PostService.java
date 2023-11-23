package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.CreatePostDto;
import faang.school.postservice.dto.post.KafkaPostView;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.PostViewEventDto;
import faang.school.postservice.dto.post.RedisPostDto;
import faang.school.postservice.dto.post.UpdatePostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.messaging.KafkaPostViewProducer;
import faang.school.postservice.publisher.PostViewEventPublisher;
import faang.school.postservice.messaging.publishing.NewPostPublisher;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRedisRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.ad.AdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {
    private final AdRepository adRepository;
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;
    private final NewPostPublisher newPostPublisher;
    private final PostViewEventPublisher postViewEventPublisher;
    private final PostRedisRepository postRedisRepository;
    private final KafkaPostViewProducer kafkaPostViewProducer;

    @Transactional
    public PostDto createPost(CreatePostDto createPostDto) {
        Post post = postMapper.toEntity(createPostDto);
        if (createPostDto.getAuthorId() != null && createPostDto.getProjectId() != null) {
            throw new DataValidationException("The author can be either a user or a project");
        }
        if (createPostDto.getAuthorId() != null && userServiceClient.getUserInternal(createPostDto.getAuthorId()) == null) {
            throw new DataValidationException("Author must be Existing on the user's system = " + createPostDto.getAuthorId()
                    + " or project ID now it = " + createPostDto.getProjectId());
        }
        if (createPostDto.getProjectId() != null && projectServiceClient.getProject(createPostDto.getProjectId()) == null) {
            throw new DataValidationException("You must provide an author ID, now this is = " + createPostDto.getAuthorId()
                    + " or project ID now it = " + createPostDto.getProjectId());
        }
        post.setDeleted(false);
        post.setPublished(false);
        PostDto result = postMapper.toDto(postRepository.save(post));
        newPostPublisher.publish(result);
        postRedisRepository.save(postMapper.toRedisPostDto(result));
        return result;
    }

    @Transactional
    public List<PostDto> publishPost() {
        List<Post> readyToPublish = postRepository.findReadyToPublish();
        if (readyToPublish.isEmpty()) {
            return new ArrayList<>();
        }
        for (Post post : readyToPublish) {
            if (post.isPublished()) {
                continue;
            }
            post.setPublished(true);
            post.setPublishedAt(null);
            postRepository.save(post);
        }
        return postMapper.toDtoList(readyToPublish);
    }

    @Transactional
    public PostDto updatePost(Long id, UpdatePostDto updatePostDto) {
        Post postInTheDatabase = postRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("Update Post not found"));

        postInTheDatabase.setContent(updatePostDto.getContent());
        postInTheDatabase.setUpdatedAt(null);

        postInTheDatabase.setAd(adRepository.findById(updatePostDto.getAdId())
                .orElseThrow(() -> new DataValidationException("Update ad not found")));

        return postMapper.toDto(postRepository.save(postInTheDatabase));
    }

    @Transactional
    public PostDto softDeletePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new DataValidationException("Delete Post not found"));
        post.setDeleted(true);
        postRepository.save(post);
        return postMapper.toDto(post);
    }

    @Transactional(readOnly = true)
    public PostDto getPostById(Long id) {
        Post postById = postRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("'Post not in database' error occurred while fetching post"));

        PostViewEventDto eventDto = PostViewEventDto.builder()
                .viewDate(LocalDateTime.now())
                .postId(id)
                .build();

        postViewEventPublisher.publish(eventDto);
        KafkaPostView kafkaPostView = KafkaPostView.builder().postId(id).authorId(postById.getAuthorId()).build();
        kafkaPostViewProducer.sendMessage(kafkaPostView);
        return postMapper.toDto(postById);
    }
    @Transactional(readOnly = true)
    public List<PostDto> getAllPostsByAuthorId(Long authorId) {
        return postRepository.findByAuthorId(authorId).stream()
                .filter(post -> !post.isDeleted() && !post.isPublished())
                .map(postMapper::toDto)
                .sorted(Comparator.comparing(PostDto::getCreatedAt).reversed())
                .toList();
    }
    @Transactional(readOnly = true)
    public List<PostDto> getAllPostsByProjectId(Long projectId) {
        return postRepository.findByProjectId(projectId).stream()
                .filter(post -> !post.isDeleted() && !post.isPublished())
                .map(postMapper::toDto)
                .sorted(Comparator.comparing(PostDto::getCreatedAt).reversed())
                .toList();
    }
    @Transactional(readOnly = true)
    public List<PostDto> getAllPostsByAuthorIdAndPublished(Long authorId) {
        return postRepository.findByAuthorId(authorId).stream()
                .filter(post -> !post.isDeleted() && post.isPublished())
                .map(postMapper::toDto)
                .sorted(Comparator.comparing(PostDto::getCreatedAt).reversed())
                .toList();
    }
    @Transactional(readOnly = true)
    public List<PostDto> getAllPostsByProjectIdAndPublished(Long projectId) {
        return postRepository.findByProjectId(projectId).stream()
                .filter(post -> !post.isDeleted() && post.isPublished())
                .map(postMapper::toDto)
                .sorted(Comparator.comparing(PostDto::getCreatedAt).reversed())
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<PostDto> getAllPostsByHashtagId(String content, Pageable pageable){
        return postRepository.findByHashtagsContent(content, pageable).map(postMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Post getPostByIdInternal(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("'Post not in database' error occurred while fetching post"));
    }

    @Transactional
    public Post updatePostInternal(Post post){
        return postRepository.save(post);
    }
}