package faang.school.postservice.service.post;

import faang.school.postservice.dto.filter.PostFilterDto;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.filter.post.PostFilter;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {
    private final PostRepository postRepository;
    private final PostValidator validator;
    private final PostMapper mapper;
    private final List<PostFilter> postFilters;

    public PostDto create(PostDto dto) {
        validator.validateBeforeCreate(dto);

        Post entity = mapper.toEntity(dto);

        Post createdEntity = postRepository.save(entity);
        log.info("Created a post: {}", createdEntity);

        return mapper.toDto(createdEntity);
    }

    public PostDto publish(Long postId) {
        Post entity = getEntityFromDB(postId);
        validator.validateBeforePublishing(entity);

        entity.setPublished(true);
        entity.setPublishedAt(LocalDateTime.now());

        Post publishedPost = postRepository.save(entity);
        log.info("Published post: {}", publishedPost);

        return mapper.toDto(publishedPost);
    }

    public PostDto update(PostDto dto) {
        Post entity = getEntityFromDB(dto.getId());
        validator.validateBeforeUpdate(dto, entity);

        entity.setContent(dto.getContent());

        Post updatedEntity = postRepository.save(entity);
        log.info("Updated post: {}", updatedEntity);

        return mapper.toDto(updatedEntity);
    }

    public PostDto delete(Long postId) {
        Post entity = getEntityFromDB(postId);
        validator.validateBeforeDeleting(entity);

        entity.setPublished(false);
        entity.setDeleted(true);

        Post deletedEntity = postRepository.save(entity);
        log.info("Deleted post: {}", deletedEntity);

        return mapper.toDto(deletedEntity);
    }

    public PostDto getPost(Long postId) {
        return mapper.toDto(getEntityFromDB(postId));
    }

    public List<PostDto> getFilteredPosts(PostFilterDto filters) {
        List<PostFilter> actualPostFilters = postFilters.stream()
                .filter(f -> f.isApplicable(filters)).toList();

        return StreamSupport.stream(postRepository.findAll().spliterator(), false)
                .filter(post -> actualPostFilters.stream()
                        .allMatch(filter -> filter.test(post, filters)))
                .map(mapper::toDto)
                .toList();
    }

    private Post getEntityFromDB(Long postId) {
        return postRepository.findById(postId).orElseThrow(() -> new EntityNotFoundException("Такого сообщения не существует."));
    }
}
