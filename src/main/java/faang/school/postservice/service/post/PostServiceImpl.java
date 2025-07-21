package faang.school.postservice.service.post;

import faang.school.postservice.dto.post.CreatePostDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.UpdatePostDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.PostValidator;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Component
@RequiredArgsConstructor
@Slf4j
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final PostValidator postValidator;
    private final PostMapper postMapper;

    @Override
    public PostDto create(CreatePostDto createPostDto) {
        postValidator.validateCreate(createPostDto);

        Post post = postMapper.toPost(createPostDto);
        postRepository.save(post);

        log.info("Post id: {} created", post.getId());
        return postMapper.toPostDto(post);
    }

    @Override
    public PostDto publish(@NonNull Long postId) {
        Post post = getNonDeletedPostByIdOrFail(postId);
        postValidator.validatePublish(post);

        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        postRepository.save(post);

        log.info("Post id: {} published", post.getId());
        return postMapper.toPostDto(post);
    }

    @Override
    public PostDto update(UpdatePostDto updatePostDto) {
        Post post = getNonDeletedPostByIdOrFail(updatePostDto.id());
        postValidator.validateUpdate(post);

        postMapper.update(updatePostDto, post);
        postRepository.save(post);

        log.info("Post id: {} updated", post.getId());
        return postMapper.toPostDto(post);
    }

    private Post getNonDeletedPostByIdOrFail(@NonNull Long postId) {
        Optional<Post> optionalPost = postRepository.findByIdAndDeletedFalse(postId);
        if (optionalPost.isEmpty()) {
            throw new EntityNotFoundException(
                    String.format("Post with postId: %d not found.", postId)
            );
        }
        return optionalPost.get();
    }

    @Override
    public void delete(@NotNull Long postId) {
        Post post = getNonDeletedPostByIdOrFail(postId);

        post.setDeleted(true);
        postRepository.save(post);

        log.info("Post id: {} marked as deleted", post.getId());
    }

    @Override
    public PostDto getById(@NotNull Long postId) {
        return postMapper.toPostDto(getNonDeletedPostByIdOrFail(postId));
    }

    @Override
    public List<PostDto> getDraftsByUser(@NotNull Long userId) {
        return postRepository.findByAuthorIdAndDeletedFalseOrderByCreatedAtDesc(userId).stream()
                .map(postMapper::toPostDto)
                .toList();
    }

    @Override
    public List<PostDto> getDraftsByProject(@NotNull Long projectId) {
        return postRepository.findByProjectIdAndDeletedFalseOrderByCreatedAtDesc(projectId).stream()
                .map(postMapper::toPostDto)
                .toList();
    }

    @Override
    public List<PostDto> getPublishedByUser(@NotNull Long userId) {
        return postRepository.findByAuthorIdAndPublishedTrueAndDeletedFalseOrderByPublishedAtDesc(userId).stream()
                .map(postMapper::toPostDto)
                .toList();
    }

    @Override
    public List<PostDto> getPublishedByProject(@NotNull Long projectId) {
        return postRepository.findByProjectIdAndPublishedTrueAndDeletedFalseOrderByPublishedAtDesc(projectId).stream()
                .map(postMapper::toPostDto)
                .toList();
    }
}
