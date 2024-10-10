package faang.school.postservice.service.impl.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.PostService;
import faang.school.postservice.service.HashtagService;
import faang.school.postservice.service.post.async.PostServiceAsync;
import faang.school.postservice.validator.post.PostValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final HashtagService hashtagService;
    private final PostValidator postValidator;
    private final PostServiceAsync postServiceAsync;

    @Value("${post.correcter.posts-batch-size}")
    private int batchSize;

    @Override
    @Transactional
    public PostDto createDraftPost(PostDto postDto) {
        postValidator.createDraftPostValidator(postDto);

        Post post = postMapper.toEntity(postDto);

        post.setPublished(false);

        return postMapper.toDto(postRepository.save(post));
    }

    @Override
    @Transactional
    public PostDto publishPost(PostDto postDto) {
        Post post = getPostFromRepository(postDto.id());

        postValidator.publishPostValidator(post);

        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());

        postRepository.save(post);
        hashtagService.createHashtags(post);

        return postMapper.toDto(post);
    }

    @Override
    @Transactional
    public PostDto updatePost(PostDto postDto) {
        Post post = getPostFromRepository(postDto.id());

        postValidator.updatePostValidator(post, postDto);

        post.setTitle(postDto.title());
        post.setContent(postDto.content());

        postRepository.save(post);
        hashtagService.updateHashtags(post);

        return postMapper.toDto(post);
    }

    @Override
    @Transactional
    public PostDto softDeletePost(Long postId) {
        Post post = getPostFromRepository(postId);

        post.setPublished(false);
        post.setDeleted(true);

        return postMapper.toDto(postRepository.save(post));
    }

    @Override
    @Transactional
    public PostDto getPost(Long id) {
        Post post = getPostFromRepository(id);

        return postMapper.toDto(post);
    }

    @Override
    @Transactional
    public List<PostDto> getAllDraftsByAuthorId(Long userId) {
        postValidator.validateIfAuthorExists(userId);

        List<PostDto> posts = postRepository.findAll().stream()
                .filter(post -> Objects.equals(post.getAuthorId(), userId))
                .filter(post -> !post.isPublished())
                .filter(post -> !post.isDeleted())
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .map(postMapper::toDto)
                .toList();

        return posts;
    }

    @Override
    @Transactional
    public List<PostDto> getAllDraftsByProjectId(Long projectId) {
        postValidator.validateIfProjectExists(projectId);

        List<PostDto> posts = postRepository.findAll().stream()
                .filter(post -> Objects.equals(post.getProjectId(), projectId))
                .filter(post -> !post.isPublished())
                .filter(post -> !post.isDeleted())
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .map(postMapper::toDto)
                .toList();

        return posts;
    }

    @Override
    @Transactional
    public List<PostDto> getAllPublishedPostsByAuthorId(Long userId) {
        postValidator.validateIfAuthorExists(userId);

        List<PostDto> posts = postRepository.findAll().stream()
                .filter(post -> Objects.equals(post.getAuthorId(), userId))
                .filter(Post::isPublished)
                .filter(post -> !post.isDeleted())
                .sorted(Comparator.comparing(Post::getPublishedAt).reversed())
                .map(postMapper::toDto)
                .toList();

        return posts;
    }

    @Override
    @Transactional
    public List<PostDto> getAllPublishedPostsByProjectId(Long projectId) {
        postValidator.validateIfProjectExists(projectId);

        List<PostDto> posts = postRepository.findAll().stream()
                .filter(post -> Objects.equals(post.getProjectId(), projectId))
                .filter(Post::isPublished)
                .filter(post -> !post.isDeleted())
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .map(postMapper::toDto)
                .toList();

        return posts;
    }

    @Override
    public List<PostDto> getPostsByHashtag(String hashtag) {
        return hashtagService.findPostsByHashtag(hashtag);
    }

    @Override
    @Transactional
    public void correctUnpublishedPosts() {
        List<Post> postsToCorrect = postRepository.findReadyToPublish();
        ListUtils.partition(postsToCorrect, batchSize)
                .forEach(postServiceAsync::correctUnpublishedPostsByBatches);
    }

    private Post getPostFromRepository(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("Post not found with id: " + postId));
    }
}
