package faang.school.postservice.util.validator;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.util.exception.CreatePostException;
import faang.school.postservice.util.exception.DeletePostException;
import faang.school.postservice.util.exception.GetPostException;
import faang.school.postservice.util.exception.PublishPostException;
import faang.school.postservice.util.exception.UpdatePostException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PostServiceValidator {

    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;
    private final PostRepository postRepository;

    public void validateToAdd(PostDto dto) {
        if (dto.getAuthorId() != null && dto.getProjectId() != null ||
                dto.getAuthorId() == null && dto.getProjectId() == null) {
            throw new CreatePostException("There is should be only one author");
        }
        if (dto.getAuthorId() != null) {
            userServiceClient.getUser(dto.getAuthorId()); // если такого пользователя или эндпоинта нет, то выбросит FeignException, я его поймаю в ExceptionHandler
        }
        if (dto.getProjectId() != null) {
            projectServiceClient.getProject(dto.getProjectId());
        }
    }

    public Post validateToPublish(Long id) {
        Post postById = postRepository.findById(id)
                .orElseThrow(() -> new PublishPostException("Post not found"));

        if (postById.isPublished()) {
            throw new PublishPostException("Post is already published");
        }

        if (postById.isDeleted()) {
            throw new PublishPostException("Post is already deleted");
        }

        return postById;
    }

    public Post validateToUpdate(Long id, String content) {
        Post postById = postRepository.findById(id)
                .orElseThrow(() -> new UpdatePostException("Post not found"));

        if (postById.isDeleted()) {
            throw new UpdatePostException("Post is already deleted");
        }
        if (!postById.isPublished()) {
            throw new UpdatePostException("Post is in draft state. It can't be updated");
        }
        if (postById.getContent().equals(content)) {
            throw new UpdatePostException("There is no changes to update");
        }

        return postById;
    }

    public Post validateToDelete(Long id) {
        Post postById = postRepository.findById(id)
                .orElseThrow(() -> new DeletePostException("Post not found"));

        if (postById.isDeleted()) {
            throw new DeletePostException("Post is already deleted");
        }
        if (!postById.isPublished()) {
            throw new DeletePostException("Post is in draft state. It can't be deleted");
        }

        return postById;
    }

    public Post validateToGet(Long id) {
        Post postById = postRepository.findById(id)
                .orElseThrow(() -> new GetPostException("Post not found"));

        if (postById.isDeleted()) {
            throw new GetPostException("Post is already deleted");
        }
        if (!postById.isPublished()) {
            throw new GetPostException("Post is in draft state. It can't be gotten");
        }

        return postById;
    }
}
