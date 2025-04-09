package faang.school.postservice.service.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.PostService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserServiceClient userClient;
    private final ProjectServiceClient projectClient;
    private final PostMapper postMapper;

    public PostDto createDraft(PostDto postDto) {
        checkOwnerPost(postDto);

        Post post = postMapper.toEntity(postDto);
        post.setPublished(false);
        return postMapper.toDto(postRepository.save(post));
    }

    public void checkOwnerPost(PostDto postDto) {
        if (postDto.getAuthorId() != null) {
            checkAuthorPost(postDto);
        } else if (postDto.getProjectId() != null) {
            checkProjectPost(postDto);
        }
    }

    public void checkAuthorPost(PostDto postDto) {
        if (userClient.getUser(postDto.getAuthorId()) == null) {
            throw new EntityNotFoundException("Нет такого пользователя");
        }
    }

    public void checkProjectPost(PostDto postDto) {
        if (projectClient.getProject(postDto.getProjectId()) == null) {
            throw new EntityNotFoundException("Проект не существует");
        }
    }

    public PostDto createPost(Long id) {
        Post post = postRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Нет такого черновика"));

        post.setPublished(true);
        return postMapper.toDto(post);
    }

    public PostDto updatePost(PostDto postDto) {
        Post postUpdate = postRepository.findById(postDto.getId()).orElseThrow(
                () -> new EntityNotFoundException("Нет такого поста"));

        postUpdate.setContent(postDto.getContent());
        Post updatedPost = postRepository.save(postUpdate);
        return postMapper.toDto(updatedPost);
    }

    public void softDeletePost(Long id) {
        Post post = postRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Нет поста")); {

            post.setDeleted(true);
        }
    }

    public PostDto getPostById(Long id) {
        Post post = postRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Нет такого поста"));

        return postMapper.toDto(post);
    }
}
