package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.exception.IncorrectIdException;
import faang.school.postservice.exception.SamePostAuthorException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserServiceClient userService;
    private final ProjectServiceClient projectService;
    private final PostMapper postMapper;

    public PostDto crateDraftPost(PostDto postDto) {
        validateData(postDto);

        Post savedPost = postRepository.save(postMapper.toPost(postDto));
        return postMapper.toDto(savedPost);
    }

    private void validateData(PostDto postDto) {
        Long authorId = postDto.getAuthorId();
        Long projectId = postDto.getProjectId();

        if (authorId != null && projectId != null) {
            throw new SamePostAuthorException("Автором поста не может быть одновременно пользователь и проект");
        }
        if (authorId != null) {
            try {
                userService.getUser(authorId);
            } catch (FeignException e) {
                throw new IncorrectIdException("Данный пользователь не найден");
            }
        } else {
            try {
                projectService.getProject(projectId);
            } catch (FeignException e) {
                throw new IncorrectIdException("Данный проект не найден");
            }
        }
    }
}
