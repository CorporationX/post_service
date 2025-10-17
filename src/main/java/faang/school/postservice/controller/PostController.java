package faang.school.postservice.controller;

import faang.school.postservice.dto.post.CreatePostRequestDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.dto.post.UpdatePostRequestDto;
import faang.school.postservice.service.PostService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * REST-контроллер для управления постами пользователей и проектов.
 */
@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
@Validated
public class PostController {
    private final PostService postService;


    /**
     * Создание черновика
     */
    @PostMapping("/posts")
    public PostResponseDto create(@Valid @RequestBody CreatePostRequestDto dto) {
        return postService.createDraft(dto);
    }


    /**
     * Публикация поста
     */
    @PutMapping("/posts/{id}/publish")
    public PostResponseDto publish(@PathVariable @Positive long id) {
        return postService.publish(id);
    }


    /**
     * Обновление контента поста
     */
    @PutMapping("/posts/{id}")
    public PostResponseDto update(@PathVariable @Positive long id,
                                  @Valid @RequestBody UpdatePostRequestDto dto) {
        return postService.update(id, dto);
    }


    /**
     * Мягкое удаление поста
     */
    @DeleteMapping("/posts/{id}")
    public void softDelete(@PathVariable @Positive long id) {
        postService.softDelete(id);
    }


    /**
     * Получение поста по id
     */
    @GetMapping("/posts/{id}")
    public PostResponseDto getById(@PathVariable @Positive long id) {
        return postService.getById(id);
    }


    /**
     * Все черновики пользователя
     */
    @GetMapping("/users/{userId}/posts/drafts")
    public List<PostResponseDto> draftsByUser(@PathVariable @Positive long userId) {
        return postService.getDraftsByUser(userId);
    }


    /**
     * Все черновики проекта
     */
    @GetMapping("/projects/{projectId}/posts/drafts")
    public List<PostResponseDto> draftsByProject(@PathVariable @Positive long projectId) {
        return postService.getDraftsByProject(projectId);
    }


    /**
     * Все опубликованные посты пользователя
     */
    @GetMapping("/users/{userId}/posts/published")
    public List<PostResponseDto> publishedByUser(@PathVariable @Positive long userId) {
        return postService.getPublishedByUser(userId);
    }


    /**
     * Все опубликованные посты проекта
     */
    @GetMapping("/projects/{projectId}/posts/published")
    public List<PostResponseDto> publishedByProject(@PathVariable @Positive long projectId) {
        return postService.getPublishedByProject(projectId);
    }
}