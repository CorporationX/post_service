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

    public void createPostDraft(PostDto postDto) {
        postValidator.validatePostContent(postDto);
        postRepository.save(postMapper.toEntity(postDto));
    }

    @Transactional
    public void publishPost(long postId) {
        Post post = getPostById(postId);
        post.setPublished(true);
    }

    @Transactional
    public void updatePost(long postId, PostDto postDto) {
        Post post = getPostById(postId);
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
    public List<Post> getAllAuthorDrafts(long authorId) {
        List<Post> authorPosts = postRepository.findByAuthorId(authorId);
        return authorPosts.stream()
                .filter(post ->!post.isPublished())
                .filter(post ->!post.isDeleted())
                .sorted(Comparator.comparing(Post::getPublishedAt)).toList();
    }

    @Transactional
    public List<Post> getAllProjectDrafts(long projectId) {
        List<Post> authorPosts = postRepository.findByProjectId(projectId);
        return authorPosts.stream()
                .filter(post ->!post.isPublished())
                .filter(post ->!post.isDeleted())
                .sorted(Comparator.comparing(Post::getPublishedAt)).toList();
    }

    @Transactional
    public List<Post> getAllAuthorPosts(long authorId) {
        List<Post> authorPosts = postRepository.findByAuthorId(authorId);
        return authorPosts.stream()
                .filter(Post::isPublished)
                .filter(post ->!post.isDeleted())
                .sorted(Comparator.comparing(Post::getPublishedAt)).toList();
    }

    @Transactional
    public List<Post> getAllProjectPosts(long projectId) {
        List<Post> authorPosts = postRepository.findByProjectId(projectId);
        return authorPosts.stream()
                .filter(Post::isPublished)
                .filter(post ->!post.isDeleted())
                .sorted(Comparator.comparing(Post::getPublishedAt)).toList();
    }
}
