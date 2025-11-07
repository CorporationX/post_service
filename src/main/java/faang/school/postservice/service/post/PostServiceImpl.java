package faang.school.postservice.service.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;


import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import faang.school.postservice.dto.post.CreatePostDto;
import faang.school.postservice.dto.post.UpdatePostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.function.Function;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    public final PostRepository postRepository;
    public final PostMapper mapper;

    @Override
    @Transactional
    public PostDto create(CreatePostDto dto) {
        if (dto.authorId() != null && dto.projectId() != null) {
            log.warn("Attempting to create a post with both authorId=%d and postId=%d".formatted(dto.authorId(), dto.projectId()));
            throw new DataValidationException("The author of a post can be either a project or a user, but not both");
        }
        Post newPost = mapper.toModel(dto);
        postRepository.save(newPost);
        return mapper.toDto(newPost);
    }

    @Override
    @Transactional
    public void publish(long postId) {
        Post post = getPostByIdOrThrow(postId);
        if (post.isPublished()) {
            log.warn("Attempting to publish already published project (projectId=%d)".formatted(postId));
            throw new DataValidationException("This project is already published"); 
        }
        if (post.isDeleted()) {
            log.warn("Attempting to publish deleted project (projectId=%d)".formatted(postId));
            throw new DataValidationException("Can not publish deleted project");
        }
        post.setPublished(true);
        postRepository.save(post);
    }

    @Override
    @Transactional
    public PostDto update(long postId, UpdatePostDto dto) {
        Post post = getPostByIdOrThrow(postId);
        mapper.updateModel(dto, post);
        return mapper.toDto(post);
    }
    
    @Override
    @Transactional
    public void softDelete(long postId) {
        Post post = getPostByIdOrThrow(postId);
        if (post.isDeleted()) {
            log.warn("Attempting to delete already deleted project (projectId=%d)".formatted(postId));
            throw new DataValidationException("Post (postId=%d) is already deleted".formatted(postId));
        } 
        post.setDeleted(true);
        post.setPublished(false);
        postRepository.save(post);
    }

    @Override
    @Transactional
    public void restore(long postId) {
        Post post = getPostByIdOrThrow(postId);
        if (!post.isDeleted()) {
            log.warn("Attempting to restore project (projectId=%d) that was not deleted".formatted(postId));
            throw new DataValidationException("Post (postId=%d) was not deleted to be recovered".formatted(postId));
        } 
        post.setDeleted(false);
        postRepository.save(post);
    }
     
    @Override
    @Transactional
    public void delete(long postId) {
        Post post = getPostByIdOrThrow(postId);
        postRepository.delete(post);
    }

    @Override
    public Post getPostByIdOrThrow(long postId) {
        return postRepository.findById(postId)
            .orElseThrow(() -> new EntityNotFoundException("No model found for the specified %d id!".formatted(postId)));
    }

    @Override
    public List<PostDto> getPostsByAuthorId(long authorId, boolean deleted, boolean published, int page, int size, String sortBy, String sortDirection) {
        return getPosts(
                pageable -> postRepository.findByAuthorIdAndDeletedStatusAndPublished(authorId, deleted, published, pageable),
                page, size, sortBy, sortDirection
                );
    }

    @Override
    public List<PostDto> getPostsByProjectId(long projectId, boolean deleted, boolean published, int page, int size, String sortBy, String sortDirection) {
        return getPosts(
                pageable -> postRepository.findByProjectIdAndDeletedStatusAndPublished(projectId, deleted, published, pageable),
                page, size, sortBy, sortDirection
                );
    }

    private List<PostDto> getPosts(Function<Pageable, Page<Post>> repositoryRequest, int page, int size, String sortBy, String sortDirection) {
        Pageable pageable = PageRequest.of(
                page, size,
                Sort.by(Sort.Direction.fromString(sortDirection), sortBy)
                );
        Page<Post> postPage = repositoryRequest.apply(pageable);  
        
        return postPage.map(mapper::toDto).getContent();
    }
}
