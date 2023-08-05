package faang.school.postservice.controller;


import faang.school.postservice.dto.post.CreatePostDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.UpdatePostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.zip.DataFormatException;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    //Создание черновика поста.
    // Ровно один автор.
    // Автором может быть либо пользователь, либо проект.
    // Наполнение поста не может быть пустым.
    // Автор должен быть существующим в системе пользователем или проектом.
    public PostDto createPost(CreatePostDto postDto) {
        validateCreatePost(postDto);
        return postService.createPost(postDto);
    }

    //Публикация поста.
    //Опубликовать можно любой существующий пост.
    // Нельзя ещё раз опубликовать пост, который уже был опубликован ранее.
    // Запомнить дату публикации.

    public List<PostDto> publishPost() {
        return postService.publishPost();
    }

    //Обновление поста (например, можно изменить текст).
    // Нельзя изменить автора поста.
    // Нельзя удалить автора поста.
    public PostDto updatePost(UpdatePostDto postDto) {
        validateUpdatePost(postDto);
        return postService.updatePost(postDto);
    }

    //Мягкое удаление поста по id.
    // Не удаляем из БД, а помечаем, как удаленный и продолжаем хранить.
    public void softDeletePost(Long postId) {
        postService.softDeletePost(postId);
    }

    //Получение поста по id.
    private PostDto getPostById(Long id) {
        return postService.getPostById(id);
    }

    //Получение всех черновиков не удаленных постов за авторством пользователя с данным id.
    // Посты должны быть отсортированы по дате создания от новых к старым.
    public List<PostDto> getAllPostsByAuthorId(Long userId) {
        return postService.getAllPostsByAuthorId(userId);
    }

    //Получение всех черновиков не удаленных постов за авторством проекта с данным id.
    // Посты должны быть отсортированы по дате создания от новых к старым.
    public List<PostDto> getAllPostsByProjectId(Long projectId) {
        return postService.getAllPostsByProjectId(projectId);
    }

    //Получение всех опубликованных не удаленных постов за авторством пользователя с данным id.
    // Посты должны быть отсортированы по дате публикации от новых к старым.
    public List<PostDto> getAllPostsByAuthorIdAndPublished(Long userId) {
        return postService.getAllPostsByAuthorIdAndPublished(userId);
    }

    //Получение всех опубликованных не удаленных постов за авторством проекта с данным id.
    // Посты должны быть отсортированы по дате публикации от новых к старым.
    public List<PostDto> getAllPostsByProjectIdAndPublished(Long projectId) {
        return postService.getAllPostsByProjectIdAndPublished(projectId);
    }

    private void validateCreatePost(CreatePostDto postDto) {
        if (postDto == null) {
            throw new DataValidationException("PostDto is null");
        }
        if (postDto.getContent() == null || postDto.getContent().isEmpty()) {
            throw new DataValidationException("Content is null");
        }
        if (postDto.getAuthorId() == null && postDto.getProjectId() == null) {
            throw new DataValidationException("AuthorId and ProjectId is null");
        }
    }

    private void validateUpdatePost(UpdatePostDto postDto) {
        if (postDto == null) {
            throw new DataValidationException("PostDto is null");
        }
        if (postDto.getContent() == null || postDto.getContent().isEmpty()) {
            throw new DataValidationException("Content is null");
        }
        if (postDto.getAdId() == null || postDto.getAdId() < 1) {
            throw new DataValidationException("AdId is null");
        }
    }
}
