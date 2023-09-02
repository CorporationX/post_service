package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.corrector.external_service.TextGearsAPIService;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.BanEventPublisher;
import faang.school.postservice.repository.PostRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;
    private final TextGearsAPIService textGearsAPIService;
    private final CommentService commentService;
    private final BanEventPublisher banEventPublisher;

    @Transactional
    public PostDto createDraftPost(PostDto postDto) {
        validateIdPostDto(postDto);
        validateAuthorExist(postDto);

        Post post = postMapper.toEntity(postDto);
        return postMapper.toDto(postRepository.save(post));
    }

    @Transactional
    public PostDto publishPost(Long id) {
        Post post = getPostIfExist(id);

        if (post.isPublished() || post.isDeleted()) {
            throw new DataValidationException("Post is already published or deleted");
        }
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        return postMapper.toDto(post);
    }

    @Transactional
    public PostDto updatePost(PostDto postDto) {
        validateIdPostDto(postDto);
        validateAuthorExist(postDto);
        Post post = getPostIfExist(postDto.getId());

        post.setContent(postDto.getContent());
        post.setUpdatedAt(LocalDateTime.now());
        return postMapper.toDto(post);
    }

    @Transactional
    public PostDto softDeletePost(Long id) {
        Post post = getPostIfExist(id);
        validatePostIsDeleted(post);

        post.setDeleted(true);
        return postMapper.toDto(post);
    }

    @Transactional(readOnly = true)
    public PostDto getPostById(Long id) {
        Post post = getPostIfExist(id);
        validatePostIsDeleted(post);

        if (!post.isPublished()) {
            throw new DataValidationException("Post is not published");
        }
        return postMapper.toDto(post);
    }

    @Transactional(readOnly = true)
    public List<PostDto> getDraftPostsByUserId(Long id) {
        validateUserExist(id);
        List<Post> draftPosts = getDraftPosts(postRepository.findByAuthorId(id));

        if (draftPosts.isEmpty()) {
            throw new EntityNotFoundException("Draft post not found");
        }
        return postMapper.toDtoList(draftPosts);
    }


    @Transactional(readOnly = true)
    public List<PostDto> getDraftPostsByProjectId(Long id) {
        validateProjectExist(id);
        List<Post> draftPosts = getDraftPosts(postRepository.findByProjectId(id));

        if (draftPosts.isEmpty()) {
            throw new EntityNotFoundException("Draft post not found");
        }
        return postMapper.toDtoList(draftPosts);
    }

    @Transactional(readOnly = true)
    public List<PostDto> getPostsByUserId(Long id) {
        validateUserExist(id);
        List<Post> posts = getPublishedPosts(postRepository.findByAuthorId(id));

        if (posts.isEmpty()) {
            throw new EntityNotFoundException("Posts not found");
        }
        return postMapper.toDtoList(posts);
    }

    @Transactional(readOnly = true)
    public List<PostDto> getPostsByProjectId(Long id) {
        validateProjectExist(id);
        List<Post> posts = getPublishedPosts(postRepository.findByProjectId(id));

        if (posts.isEmpty()) {
            throw new EntityNotFoundException("Posts not found");
        }
        return postMapper.toDtoList(posts);
    }

    public Post getPostIfExist(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post with the specified id does not exist"));
    }

    private List<Post> getDraftPosts(List<Post> posts) {
        return posts.stream()
                .filter(post -> !post.isDeleted() && !post.isPublished())
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .toList();
    }

    private List<Post> getPublishedPosts(List<Post> posts) {
        return posts.stream()
                .filter(post -> !post.isDeleted() && post.isPublished())
                .sorted(Comparator.comparing(Post::getPublishedAt).reversed())
                .toList();
    }

    private void validateIdPostDto(PostDto postDto) {
        if ((postDto.getAuthorId() == null && postDto.getProjectId() == null) ||
                (postDto.getAuthorId() != null && postDto.getProjectId() != null)) {
            throw new DataValidationException("Enter one thing: authorId or projectId");
        }
    }

    private void validateAuthorExist(PostDto postDto) {
        if (postDto.getAuthorId() != null) {
            validateUserExist(postDto.getAuthorId());
        } else if (postDto.getProjectId() != null) {
            validateProjectExist(postDto.getProjectId());
        }
    }

    private void validatePostIsDeleted(Post post) {
        if (post.isDeleted()) {
            throw new DataValidationException("Post is already deleted");
        }
    }

    private void validateUserExist(Long id) {
        try {
            userServiceClient.getUser(id);
        } catch (FeignException e) {
            throw new EntityNotFoundException("User with the specified authorId does not exist");
        }
    }

    private void validateProjectExist(Long id) {
        try {
            projectServiceClient.getProject(id);
        } catch (FeignException e) {
            throw new EntityNotFoundException("Project with the specified projectId does not exist");
        }
    }

    public void processSpellCheckUnpublishedPosts() {
        List<Post> unpublishedPosts = postRepository.findReadyToPublish();

        for (Post post : unpublishedPosts) {
            String correctedText = textGearsAPIService.correctText(post.getContent());
            post.setContent(correctedText);
            postRepository.save(post);
        }
    }

    public void findCommentersAndPublishBanEvent() {
        List<Comment> unverifiedComments = commentService.getUnverifiedComments();

        Map<Long, List<Comment>> commentsByAuthor = unverifiedComments.stream()
                .collect(Collectors.groupingBy(Comment::getAuthorId));

        for (Map.Entry<Long, List<Comment>> entry : commentsByAuthor.entrySet()) {
            Long authorId = entry.getKey();
            List<Comment> authorComments = entry.getValue();

            if (authorComments.size() > 5) {
                banEventPublisher.publishBanEvent(authorId);
            }
        }
    }
}
