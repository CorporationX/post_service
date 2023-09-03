package faang.school.postservice.service;


import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.ScheduledTaskDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.mapper.ScheduledTaskMapper;
import faang.school.postservice.messaging.postevent.PostEventPublisher;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.scheduled.ScheduledTask;
import faang.school.postservice.publisher.PostViewEventPublisher;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.ScheduledTaskRepository;
import faang.school.postservice.service.HashtagService;
import faang.school.postservice.util.exception.PostNotFoundException;
import faang.school.postservice.util.validator.PostServiceValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {

    private final PostServiceValidator validator;
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final ScheduledTaskMapper scheduledTaskMapper;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;
    private final HashtagService hashtagService;
    private final PostEventPublisher postEventPublisher;
    private final PostViewEventPublisher postViewEventPublisher;
    private final ScheduledTaskRepository scheduledTaskRepository;

    @Transactional
    public PostDto addPost(PostDto dto) {
        validator.validateToAdd(dto);

        if (dto.getAuthorId() != null) {
            userServiceClient.getUser(dto.getAuthorId());
        }
        if (dto.getProjectId() != null) {
            projectServiceClient.getProject(dto.getProjectId());
        }

        Post post = postMapper.toEntity(dto);
        postRepository.save(post);
        hashtagService.parseContentToAdd(post);

        return postMapper.toDto(post);
    }

    @Transactional
    public PostDto publishPost(Long id) {
        Post postById = getPostById(id);

        validator.validateToPublish(postById);

        postById.setPublished(true);
        postById.setPublishedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

        postRepository.save(postById);

        postEventPublisher.send(postById);

        return postMapper.toDto(postById);
    }

    @Transactional
    public PostDto updatePost(Long id, String content) {
        Post postById = getPostById(id);
        validator.validateToUpdate(postById, content);
        String previousContent = postById.getContent();

        postById.setContent(content);
        postById.setUpdatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

        postRepository.save(postById);
        hashtagService.parsePostContentAndSaveHashtags(postById, previousContent);

        return postMapper.toDto(postById);
    }

    @Transactional
    public PostDto deletePost(Long id) {
        Post postById = getPostById(id);

        validator.validateToDelete(postById);

        postById.setDeleted(true);
        postById.setUpdatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

        postRepository.save(postById);

        return postMapper.toDto(postById);
    }

    public PostDto getPost(Long id){
        Post postById = getPostById(id);
        validator.validateToGet(postById);

        postViewEventPublisher.publish(postById);

        return postMapper.toDto(postById);
    }

    public List<PostDto> getDraftsByAuthorId(Long authorId){
        List<Post> draftsByAuthorId = postRepository.findReadyToPublishByAuthorId(authorId);

        return postMapper.toDtos(draftsByAuthorId);
    }

    public List<PostDto> getDraftsByProjectId(Long projectId) {
        List<Post> draftsByProjectId = postRepository.findReadyToPublishByProjectId(projectId);

        return postMapper.toDtos(draftsByProjectId);
    }

    public List<PostDto> getPostsByAuthorId(Long authorId){
        List<Post> postsByAuthorId = postRepository.findPublishedPostsByAuthorId(authorId);

        postsByAuthorId.forEach(postViewEventPublisher::publish);

        return postMapper.toDtos(postsByAuthorId);
    }

    public List<PostDto> getPostsByProjectId(Long projectId) {
        List<Post> postsByProjectId = postRepository.findPublishedPostsByProjectId(projectId);

        postsByProjectId.forEach(postViewEventPublisher::publish);

        return postMapper.toDtos(postsByProjectId);
    }
    @Transactional
    public List<Post> getAllPosts(){
        return postRepository.findAll();
    }

    @Transactional
    public void save(Post post){
        postRepository.save(post);
    }

    @Transactional
    public ScheduledTaskDto actWithScheduledPost(ScheduledTaskDto dto) {
        Optional<Post> postById = postRepository.findById(dto.entityId());
        Optional<ScheduledTask> scheduledPostById = scheduledTaskRepository
                .findScheduledTaskById(dto.entityId(), dto.entityType());

        validator.validateToActWithPostBySchedule(postById, dto.entityId(), scheduledPostById);

        ScheduledTask task = scheduledTaskMapper.toEntity(dto);

        ScheduledTask entity = scheduledTaskRepository.save(task);

        return scheduledTaskMapper.toDto(entity);
    }

    @Async()
    public CompletableFuture<Void> publishScheduledPosts() {
        return null; // TODO: 08.08.2023
    }

    private Post getPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException("Post with id " + String.format("%d", id) + " not found"));
    }
}
