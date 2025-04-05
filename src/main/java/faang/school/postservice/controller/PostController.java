package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.service.LikeService;
import faang.school.postservice.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@Tag(name = "Post Controller", description = "Управление постами")
@RequestMapping("${domain.base-path}/post")
public class PostController {
    private final PostService postService;
    private final LikeService likeService;

    @GetMapping(value = "/feed")
    public List<PostDto> getFeed(@RequestBody PostDto postDto) {
        return postService.getFeed(postDto);
    }

    @PostMapping(value = "/like")
    public void like(@RequestBody PostDto postDto) {
        likeService.addLike(postDto);
    }

    @PostMapping(value = "/create-draft")
    @Operation(summary = "Создать пост", description = "Создает новый пост")
    public PostDto createDraft(@Valid @RequestBody PostDto postDto) {
        return postService.createDraft(postDto);
    }

    @PutMapping(value = "/publish/{postId}")
    @Operation(summary = "Опубликовать пост", description = "Публикует ранее созданный черновик")
    public PostDto publish(@PathVariable @NotNull(message = "postId can't be null") Long postId) {
        return postService.publish(postId);
    }

    @PatchMapping(value = "/update")
    @Operation(summary = "Обновление поста", description = "Обновление текст поста")
    public PostDto update(@RequestParam Long id, @RequestParam String content) {
        return postService.update(id, content);
    }

    @PatchMapping(value = "/soft-delete/{id}")
    @Operation(summary = "Мягкое удаление поста", description = "Изменяет статус поста в удалённый")
    public PostDto softDelete(@PathVariable Long id) {
        return postService.softDelete(id);
    }

    @GetMapping(value = "/{id}")
    @Operation(summary = "Получить пост по ID", description = "Возвращает пост по его идентификатору")
    public PostDto getById(@PathVariable Long id) {
        return postService.getById(id);
    }

    @GetMapping(value = "/drafts-by-user/{userId}")
    @Operation(summary = "Получить не удалённые черновики пользователя",
            description = "Возвращает не удалённые черновики по userId пользователя")
    public List<PostDto> getNotDeletedDraftByUserId(@PathVariable Long userId) {
        return postService.getNotDeletedDraftsByUserId(userId);
    }

    @GetMapping(value = "/drafts-by-project/{projectId}")
    @Operation(summary = "Получить не удалённые черновики проекта",
            description = "Возвращает черновики по projectId проекта")
    public List<PostDto> getNotDeletedDraftsByProjectId(@PathVariable Long projectId) {
        return postService.getNotDeletedDraftsByProjectId(projectId);
    }

    @GetMapping(value = "/published-by-user/{userId}")
    @Operation(summary = "Получить опубликованные и не удалённые посты пользователя",
            description = "Возвращает посты по userId пользователя")
    public List<PostDto> getNotDeletedPublishedPostsByUserId(@PathVariable Long userId) {
        return postService.getNotDeletedPublishedPostsByUserId(userId);
    }

    @GetMapping(value = "/published-by-project/{projectId}")
    @Operation(summary = "Получить опубликованные и не удалённые посты проекта",
            description = "Возвращает посты по projectId проекта")
    public List<PostDto> getNotDeletedPublishedPostsByProjectId(@PathVariable Long projectId) {
        return postService.getNotDeletedPublishedPostsByProjectId(projectId);
    }
}
