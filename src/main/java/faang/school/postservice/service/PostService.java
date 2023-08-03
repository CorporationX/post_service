package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;

    //Создание черновика поста.
    // Ровно один автор.
    // Автором может быть либо пользователь, либо проект.
    // Наполнение поста не может быть пустым.
    // Автор должен быть существующим в системе пользователем или проектом.
    public PostDto createPost(PostDto postDto) {
        if ((postDto.getAuthorId() == null) == (postDto.getProjectId() == null)) {
            if ((userServiceClient.getUser(postDto.getAuthorId()) == null)
                    == (projectServiceClient.getProject(postDto.getProjectId()) == null)) {
                throw new DataValidationException("You must provide an author ID, now this is = " + postDto.getAlbumsId()
                        + " or project ID now it = " + postDto.getProjectId());
            }
        }
        postDto.setDeleted(false);
        postDto.setPublished(false);
        return postMapper.toDto(postRepository.save(postMapper.toEntity(postDto)));
    }


    //Публикация поста.
    //Опубликовать можно любой существующий пост.
    // Нельзя ещё раз опубликовать пост, который уже был опубликован ранее.
    // Запомнить дату публикации.
    public PostDto publishPost(Long postId) {
        List<Post> readyToPublish = postRepository.findReadyToPublish();
        for (Post post : readyToPublish) {
            if (postId.equals(post.getId()) && !post.isDeleted() && !post.isPublished()) {
                post.setPublished(true);
                post.setDeleted(true);
                postRepository.save(post);
                return postMapper.toDto(post);
            }
        }
        throw new DataValidationException("Post not found");
    }


    //Обновление поста (например, можно изменить текст).
    // Нельзя изменить автора поста.
    // Нельзя удалить автора поста.
    public PostDto updatePost(Long id, PostDto postDto) {
        Post postInTheDatabase = postRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("Post not found"));
        if (postDto.getAuthorId() != postInTheDatabase.getAuthorId()) {
            throw new DataValidationException("You can't change your post");
        }
        postMapper.update(postDto, postInTheDatabase);
        return postMapper.toDto(postRepository.save(postInTheDatabase));
    }


    //Мягкое удаление поста по id.
    // Не удаляем из БД, а помечаем, как удаленный и продолжаем хранить.
    public PostDto softDeletePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new DataValidationException("Post not found"));
        post.setDeleted(true);
        postRepository.save(post);
        return postMapper.toDto(post);
    }


    //Получение поста по id.
    public PostDto getPostById(Long id) {
        if (postRepository.findById(id).isEmpty()) {
            throw new DataValidationException("Post not found");
        }
        return postMapper.toDto(postRepository.findById(id).orElseThrow());
    }


    //Получение всех черновиков не удаленных постов за авторством пользователя с данным id.
    // Посты должны быть отсортированы по дате создания от новых к старым.
    public List<PostDto> getAllPostsByAuthorId(Long authorId) {
        return postRepository.findByAuthorId(authorId).stream()
                .filter(post -> !post.isDeleted() && !post.isPublished())
                .map(postMapper::toDto)
                .sorted(Comparator.comparing(PostDto::getCreatedAt).reversed())
                .toList();
    }


    //Получение всех черновиков не удаленных постов за авторством проекта с данным id.
    // Посты должны быть отсортированы по дате создания от новых к старым.
    public List<PostDto> getAllPostsByProjectId(Long projectId) {
        return postRepository.findByProjectId(projectId).stream()
                .filter(post -> !post.isDeleted() && !post.isPublished())
                .map(postMapper::toDto)
                .sorted(Comparator.comparing(PostDto::getCreatedAt).reversed())
                .toList();
    }

    //Получение всех опубликованных не удаленных постов за авторством пользователя с данным id.
    // Посты должны быть отсортированы по дате публикации от новых к старым.
    public List<PostDto> getAllPostsByAuthorIdAndPublished(Long authorId) {
        return postRepository.findByAuthorId(authorId).stream()
                .filter(post -> !post.isDeleted() && post.isPublished())
                .map(postMapper::toDto)
                .sorted(Comparator.comparing(PostDto::getCreatedAt).reversed())
                .toList();
    }


    //Получение всех опубликованных не удаленных постов за авторством проекта с данным id.
    // Посты должны быть отсортированы по дате публикации от новых к старым.
    public List<PostDto> getAllPostsByProjectIdAndPublished(Long projectId) {
        return postRepository.findByProjectId(projectId).stream()
                .filter(post -> !post.isDeleted() && post.isPublished())
                .map(postMapper::toDto)
                .sorted(Comparator.comparing(PostDto::getCreatedAt).reversed())
                .toList();
    }
}
