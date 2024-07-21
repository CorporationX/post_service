package faang.school.postservice.service.post;

import faang.school.postservice.dto.post.PostCreateDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.PostFilterDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.dto.post.SortField;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.post.PostFilterRepository;
import faang.school.postservice.repository.post.PostRepository;
import faang.school.postservice.validation.post.PostValidation;
import faang.school.postservice.validation.project.ProjectValidation;
import faang.school.postservice.validation.user.UserValidation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final List<PostFilterRepository> postFilterRepository;
    private final PostValidation postValidation;
    private final UserValidation userValidation;
    private final ProjectValidation projectValidation;
    private final PostMapper postMapper;

    @Transactional
    public PostDto create(PostCreateDto postCreateDto) {
        if (postValidation.isNullable(postCreateDto)) {
            throw new DataValidationException("Post can't be empty");
        }

        if (!postValidation.oneOfTheAuthorsIsNoNullable(postCreateDto.getAuthorId(), postCreateDto.getProjectId())) {
            throw new DataValidationException("Only one of projectId or authorId must be provided");
        }

        if (!postValidation.isNullable(postCreateDto.getAuthorId())) {
            userValidation.doesUserExist(postCreateDto.getAuthorId());
        }

        if (!postValidation.isNullable(postCreateDto.getProjectId())) {
            projectValidation.doesProjectExist(postCreateDto.getProjectId());
        }

        Post createdPost = postRepository.save(postMapper.toPost(postCreateDto));
        return postMapper.toDto(createdPost);
    }

    @Transactional
    public PostDto publish(Long id) {
        PostDto post = postMapper.toDto(postRepository.findById(id)
                .orElseThrow(() -> new DataValidationException(String.format("Post with id %s don't exists", id))));

        if (post.isPublished()) {
            throw new DataValidationException(String.format("Post %s has already published", post.getId()));
        }

        int updatedCount = postRepository.updatePublishedStatus(post.getId(), LocalDateTime.now(), true);

        if (updatedCount == 0) {
            throw new DataValidationException("Post wasn't save");
        }

        post.setPublished(true);

        return post;
    }

    @Transactional
    public PostDto update(PostUpdateDto postDto) {
        if (postValidation.isNullable(postDto)) {
            throw new DataValidationException("Post can't be empty");
        }

        postValidation.validateForUpdating(postDto);

        Post postToUpdate = postRepository.findById(postDto.getId())
                .orElseThrow(() -> new DataValidationException(String.format("Post %s doesn't exist", postDto.getId())));

        postToUpdate.setContent(postDto.getContent());

        return postMapper.toDto(postRepository.save(postToUpdate));
    }

    @Transactional
    public void delete(Long id) {
        Post postToDelete = postRepository.findById(id)
                .orElseThrow(() -> new DataValidationException(String.format("Post %s doesn't exist", id)));
        postToDelete.setDeleted(true);

        postRepository.save(postToDelete);
    }

    @Transactional
    public PostDto getById(Long id) {
        Post postToDelete = postRepository.findById(id)
                .orElseThrow(() -> new DataValidationException(String.format("Post %s doesn't exist", id)));

        return postMapper.toDto(postToDelete);
    }

    @Transactional
    public Page<PostDto> getPostsByPublishedStatus(PostFilterDto postFilter) {
        if (postFilter == null) {
            throw new DataValidationException("Post filter can't be empty");
        }

        if (postFilter.getPublished() == null) {
            throw new DataValidationException("Published field can't be empty");
        }

        if ((postFilter.getAuthorId() == null && postFilter.getProjectId() == null) || (postFilter.getAuthorId() != null && postFilter.getProjectId() != null)) {
            throw new DataValidationException("Only one of projectId or authorId must be provided");
        }

        if (postFilter.getAuthorId() != null) {
            userValidation.doesUserExist(postFilter.getAuthorId());
        } else {
            projectValidation.doesProjectExist(postFilter.getProjectId());
        }

        postFilter.setDeleted(false);

        Optional<Specification<Post>> postSpecification = postFilterRepository.stream()
                .filter(filter -> filter.isApplicable(postFilter))
                .map(filter -> filter.apply(postFilter))
                .reduce(Specification::and);

        postSpecification.orElseThrow(() -> new DataValidationException("Required fields are empty"));

        Pageable pageRequest = postFilter.getPublished()
                ? PageRequest.of(postFilter.getPage(), postFilter.getSize(), Sort.by(SortField.PUBLISHED_AT.getValue()).descending())
                : PageRequest.of(postFilter.getPage(), postFilter.getSize(), Sort.by(SortField.CREATED_AT.getValue()).descending());

        return postRepository.findAll(postSpecification.get(), pageRequest).map(postMapper::toDto);
    }
}
