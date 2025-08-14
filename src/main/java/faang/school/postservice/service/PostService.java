package faang.school.postservice.service;

import faang.school.postservice.client.FeignClientValidator;
import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.event.PostEventDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.PostCreateDto;
import faang.school.postservice.exception.PostNotFoundException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.producer.EventProducer;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.util.Utils;
import faang.school.postservice.validator.Validator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    public static final String POST_BY_ID_NOT_FOUND = "Post by id [{}] not found";
    public static final String AUTHOR_NOT_FOUND = "author by id={} find error";
    public static final String PROJECT_NOT_FOUND = "project by id={} find error";

    private final PostRepository postRepository;
    private final FeignClientValidator feignClientValidator;
    private final Utils utils;
    private final List<Validator<PostCreateDto>> createPostValidator;
    private final PostMapper postMapper;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;
    private final EventProducer<PostEventDto> postEventProducer;

    public Post findPostById(Long postId) {
        return postRepository.findById(postId)
            .orElseThrow(() ->
                new PostNotFoundException(utils.format(POST_BY_ID_NOT_FOUND, postId)));
    }

    @Transactional
    public PostDto createPost(@Valid PostCreateDto postDto) {
        log.info("Creating post [{}]", postDto);
        createPostValidator.forEach(validator -> {
            log.debug("run validator: [{}]", validator.getClass().getName());
            validator.validate(postDto);
        });

        if (postDto.authorId() != null) {
            validateUser(postDto);
        } else {
            validateProject(postDto);
        }
        Post post = postMapper.toCreateEntity(postDto);
        Post result = postRepository.save(post);
        if (postDto.authorId() != null) {
            log.debug("publishing a user post in kafka. post: [{}]", postDto);
            publishPostEvent(result);
        }
        return postMapper.toDto(result);
    }

    private void validateUser(PostCreateDto postDto) {
        log.debug("validateUser: [{}]", postDto);
        feignClientValidator.validateById(
            () -> userServiceClient.checkUser(postDto.authorId()),
            utils.format(AUTHOR_NOT_FOUND, postDto.authorId()));
    }

    private void validateProject(PostCreateDto postDto) {
        log.debug("validateProject: [{}]", postDto);
        feignClientValidator.validateById(
            () -> projectServiceClient.checkProject(postDto.projectId()),
            utils.format(PROJECT_NOT_FOUND, postDto.projectId()));
    }

    private void publishPostEvent(Post post) {
        PostEventDto eventDto = postMapper.toEventDto(post);
        log.debug("publishPostEvent: [{}]", eventDto);
        postEventProducer.publish(eventDto);
    }
}
