package faang.school.postservice.service;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.PostValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final PostValidator postValidator;

    @Transactional
    public void createPostDraft(PostDto postDto) {
        postValidator.validatePostContent(postDto);
        postValidator.validateOwnerPost(postDto);
        postRepository.save(postMapper.toEntity(postDto));
    }

    @Transactional
    public void publishPost(long postId) {
        Post post = getPostById(postId);
        post.setPublished(true);
        postRepository.save(post);
    }

    @Transactional
    public void updatePost(long postId, PostDto postDto) {
        Post post = getPostById(postId);
        postValidator.validatePostContent(postDto);
        post.setContent(postDto.getContent());
        postRepository.save(post);
    }

    @Transactional
    public void deletePost(long postId) {
        Post post = getPostById(postId);
        post.setDeleted(true);
        postRepository.save(post);
    }

    public Post getPostById(long postId) {
        Optional<Post> postOpt = postRepository.findById(postId);
        return postOpt.orElseThrow(() -> new IllegalArgumentException("Post not found"));
    }

    @Transactional
    public List<PostDto> getAuthorDrafts(long authorId) {
        List<Post> sortedPosts = postRepository.findByAuthorId(authorId).stream()
                .filter(post -> !post.isPublished())
                .filter(post -> !post.isDeleted())
                .sorted(Comparator.comparing(Post::getPublishedAt)).toList();
        return postMapper.toDtoList(sortedPosts);
    }

    @Transactional
    public List<PostDto> getProjectDrafts(long projectId) {
        List<Post> sortedPosts = postRepository.findByProjectId(projectId).stream()
                .filter(post -> !post.isPublished())
                .filter(post -> !post.isDeleted())
                .sorted(Comparator.comparing(Post::getPublishedAt)).toList();
        return postMapper.toDtoList(sortedPosts);
    }

    @Transactional
    public List<PostDto> getAuthorPosts(long authorId) {
        List<Post> sortedPosts = postRepository.findByAuthorId(authorId).stream()
                .filter(Post::isPublished)
                .filter(post -> !post.isDeleted())
                .sorted(Comparator.comparing(Post::getPublishedAt)).toList();
        return postMapper.toDtoList(sortedPosts);
    }

    @Transactional
    public List<PostDto> getProjectPosts(long projectId) {
        List<Post> sortedPosts = postRepository.findByProjectId(projectId).stream()
                .filter(Post::isPublished)
                .filter(post -> !post.isDeleted())
                .sorted(Comparator.comparing(Post::getPublishedAt)).toList();
        return postMapper.toDtoList(sortedPosts);
    }
}
