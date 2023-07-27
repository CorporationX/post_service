package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.util.exception.DeletePostException;
import faang.school.postservice.util.exception.GetPostException;
import faang.school.postservice.util.exception.PublishPostException;
import faang.school.postservice.util.exception.UpdatePostException;
import faang.school.postservice.util.validator.PostServiceValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {

    private final PostServiceValidator validator;
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;

    @Transactional
    public PostDto addPost(PostDto dto) {
        validator.validateToAdd(dto);

        if (dto.getAuthorId() != null) {
            userServiceClient.getUser(dto.getAuthorId()); // если такого пользователя или эндпоинта нет, то выбросит FeignException, я его поймаю в ExceptionHandler
        }
        if (dto.getProjectId() != null) {
            projectServiceClient.getProject(dto.getProjectId());
        }

        Post post = postMapper.toEntity(dto);
        postRepository.save(post);

        return postMapper.toDto(post);
    }

    @Transactional
    public PostDto publishPost(Long id) {
        Post postById = postRepository.findById(id)
                .orElseThrow(() -> new PublishPostException("Post not found"));

        validator.validateToPublish(postById);

        postById.setPublished(true);
        postById.setPublishedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

        postRepository.save(postById);

        return postMapper.toDto(postById);
    }

    @Transactional
    public PostDto updatePost(Long id, String content) {
        Post postById = postRepository.findById(id)
                .orElseThrow(() -> new UpdatePostException("Post not found"));

        validator.validateToUpdate(postById, content);

        postById.setContent(content);
        postById.setUpdatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

        postRepository.save(postById);

        return postMapper.toDto(postById);
    }

    @Transactional
    public PostDto deletePost(Long id) {
        Post postById = postRepository.findById(id)
                .orElseThrow(() -> new DeletePostException("Post not found"));

        validator.validateToDelete(postById);

        postById.setDeleted(true);
        postById.setUpdatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

        postRepository.save(postById);

        return postMapper.toDto(postById);
    }

    public PostDto getPost(Long id){
        Post postById = postRepository.findById(id)
                .orElseThrow(() -> new GetPostException("Post not found"));

        validator.validateToGet(postById);

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

        return postMapper.toDtos(postsByAuthorId);
    }
}
