package faang.school.postservice.service.impl;

import faang.school.postservice.dto.post.PostDraftDto;
import faang.school.postservice.exception.NotExistsException;
import faang.school.postservice.exception.NotSupportedDataException;
import faang.school.postservice.integration.project.service.ProjectServiceClient;
import faang.school.postservice.integration.user.service.UserServiceClient;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.PostService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostServiceImpl implements PostService {

    private final PostMapper postMapper;
    private final PostRepository postRepository;
    private final ProjectServiceClient projectServiceClient;
    private final UserServiceClient userServiceClient;

    @Override
    public PostDraftDto findById(long id) {
        return postRepository.findById(id)
                .map(p -> postMapper.postToPostDraftDto(p))
                .orElseThrow(() -> new EntityNotFoundException("Пост с id " + id + " не найден"));
    }

    @Override
    public void save(PostDraftDto postDraftDto) {
        postRepository.save(postMapper.postDraftDtoToPost(postDraftDto));
    }

    @Override
    public void markPostAsDeleted(long id) {
        PostDraftDto postDraftDto = findById(id);
//        postDraftDto.setDeleted(true);
//        postDraftDto.setPublished(false);
        save(postDraftDto);
    }

    @Override
    public void createPostDraft(PostDraftDto postDraftDto) {
//        checkAuthorExists(postDraftDto);
        Post post = postMapper.postDraftDtoToPost(postDraftDto);
        post.setPublished(false);
        post.setDeleted(false);
        postRepository.save(post);
    }

    @Override
    public void publishPostDraft(Long postId) {

    }

    private void checkAuthor(PostDraftDto postDraftDto) {
        if (postDraftDto.getProjectId() == null) {
            if (postDraftDto.getAuthorId() == null) {
                throw new NotSupportedDataException("Необходимо укзать одно из этих полей: AuthorId или ProjectId");
            }
        }
    }

    private void checkAuthorExists(PostDraftDto postDraftDto) {
        checkAuthor(postDraftDto);
        if (postDraftDto.getAuthorId() != null) {
            if (userServiceClient.getUser(postDraftDto.getAuthorId()) == null) {
                throw new NotExistsException("Автор с id " + postDraftDto.getAuthorId() + " не найден в базе");
            }
        } else {
            if (projectServiceClient.getProject(postDraftDto.getProjectId()) == null) {
                throw new NotExistsException("Проект с id " + postDraftDto.getProjectId() + " не найден в базе");
            }
        }
    }
}
