package faang.school.postservice.service;


import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PictureDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.ScheduledTaskDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.mapper.ScheduledTaskMapper;
import faang.school.postservice.messaging.postevent.PostEventPublisher;
import faang.school.postservice.messaging.userbanevent.UserBanEventPublisher;
import faang.school.postservice.model.Picture;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.scheduled.ScheduledTask;
import faang.school.postservice.publisher.PostViewEventPublisher;
import faang.school.postservice.repository.PictureRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.ScheduledTaskRepository;
import faang.school.postservice.util.ConvertFile;
import faang.school.postservice.util.CoverHandler;
import faang.school.postservice.util.exception.PostNotFoundException;
import faang.school.postservice.util.validator.PostServiceValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
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
    private final PictureRepository pictureRepository;
    private final UserBanEventPublisher userBanEventPublisher;
    private final AmazonS3 s3Client;
    private final ConvertFile convertFile;
    private final CoverHandler coverHandler;
    @Value("${services.s3.bucketName}")
    private String bucketName;

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

    public PostDto getPost(Long id) {
        Post postById = getPostById(id);
        validator.validateToGet(postById);

        postViewEventPublisher.publish(postById);

        return postMapper.toDto(postById);
    }

    public List<PostDto> getDraftsByAuthorId(Long authorId) {
        List<Post> draftsByAuthorId = postRepository.findReadyToPublishByAuthorId(authorId);

        return postMapper.toDtos(draftsByAuthorId);
    }

    public List<PostDto> getDraftsByProjectId(Long projectId) {
        List<Post> draftsByProjectId = postRepository.findReadyToPublishByProjectId(projectId);

        return postMapper.toDtos(draftsByProjectId);
    }

    public List<PostDto> getPostsByAuthorId(Long authorId) {
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
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    @Transactional
    public void save(Post post) {
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

    public void banUser() {
        Map<Long, Long> banList = new HashMap<>();
        List<Post> allPosts = postRepository.findAll();
        long postCount = 0;

        for (Post allPost : allPosts) {
            Long authorId = allPost.getAuthorId();
            if (!banList.containsKey(authorId)) {
                banList.put(authorId, postCount);
            }
            if (!allPost.isVerified()) {
                long increment = banList.get(authorId) + 1;
                banList.put(authorId, increment);
            }
        }

        for (Map.Entry<Long, Long> entry : banList.entrySet()) {
            if (entry.getValue() > 5) {
                userBanEventPublisher.publish(entry.getKey());
            }
        }
    }

    @Transactional
    public Picture uploadPicture(long postId, MultipartFile file) {
        Optional<Post> postById = postRepository.findById(postId);
        coverHandler.resizeCover(file);
        String fileName = getFileName(file);

        File convertedFile = convertFile.convertMultiPartFileToFile(file);
        putFile(file, convertedFile, fileName);

        Post post = postById.get();
        Picture picture = new Picture();
        picture.setPictureName(fileName);

        post.getPictures().add(picture);
        pictureRepository.save(picture);
        postRepository.save(post);
        return picture;
    }

    public void deletePicture(long postId, PictureDto pictureName) {
        Optional<Post> postById = postRepository.findById(postId);
        Post post = postById.get();
        for (Picture picture : post.getPictures()) {
            if (Objects.equals(picture.getPictureName(), pictureName.getPictureName())) {
                deletePicture(pictureName.getPictureName());
                pictureRepository.deletePictureByName(picture.getPictureName());
                postRepository.deletePictureByName(picture.getPictureName());
            }
        }
    }

    public void deletePicture(String key) {
        try {
            s3Client.deleteObject(bucketName, key);
        } catch (AmazonClientException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void putFile(MultipartFile file, File convertedFile, String fileName) {
        long fileSize = file.getSize();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(fileSize);
        metadata.setContentType(file.getContentType());
        s3Client.putObject(new PutObjectRequest(bucketName, fileName, convertedFile));
    }

    private String getFileName(MultipartFile file) {
        return System.currentTimeMillis() + "_" + file.getOriginalFilename();
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
