package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.UpdatePostDto;
import faang.school.postservice.service.PostService;
import faang.school.postservice.validator.PostValidator;
import faang.school.postservice.validator.ResourceValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
@Tag(name = "Контроллер постов")
public class PostController {
    private final PostService postService;
    private final PostValidator postValidator;
    private final ResourceValidator resourceValidator;

    @Operation(summary = "Создать черновик поста", parameters = {@Parameter(in = ParameterIn.HEADER,
            name = "x-user-id", description = "id пользователя", required = true)})
    @PostMapping("/drafts")
    public PostDto createDraftPost(@RequestBody PostDto postDto, @RequestParam(value = "file", required = false) MultipartFile file) {
        if (file != null) {
            resourceValidator.validateResourceType(file);
        }
        postValidator.validateAuthorCount(postDto);
        postValidator.validateContentExists(postDto.getContent());
        return postService.createDraftPost(postDto, file);
    }

    @Operation(summary = "Опубликовать пост", parameters = {@Parameter(in = ParameterIn.HEADER,
            name = "x-user-id", description = "id пользователя", required = true)})
    @PutMapping("/drafts/{id}")
    public PostDto publishPost(@PathVariable long id) {
        return postService.publishPost(id);
    }

    @Operation(summary = "Обновить пост")
    @PutMapping("/{id}")
    public PostDto updatePost(@RequestBody UpdatePostDto postDto, @PathVariable long id,
                              @RequestParam(value = "file", required = false) MultipartFile file) {
        postValidator.validateContentExists(postDto.getContent());
        if (file != null) {
            resourceValidator.validateResourceType(file);
        }
        return postService.updatePost(postDto, id, file);
    }

    @Operation(summary = "Удалить пост")
    @DeleteMapping("/{id}")
    public void deletePost(@PathVariable long id) {
        postService.deletePost(id);
    }

    @Operation(summary = "Получить пост")
    @GetMapping("/{id}")
    public PostDto getPost(@PathVariable long id) {
        return postService.getPost(id);
    }

    @Operation(summary = "Получить черновики постов пользователя")
    @GetMapping("/drafts/user/{userId}")
    public List<PostDto> getDraftsByUser(@PathVariable long userId) {
        return postService.getDraftsByUser(userId);
    }

    @Operation(summary = "Получить черновики постов проекта")
    @GetMapping("/drafts/project/{projectId}")
    public List<PostDto> getDraftsByProject(@PathVariable long projectId) {
        return postService.getDraftsByProject(projectId);
    }

    @Operation(summary = "Получить опубликованные посты пользователя")
    @GetMapping("/user/{userId}")
    public List<PostDto> getPublishedPostsByUser(@PathVariable long userId) {
        return postService.getPublishedPostsByUser(userId);
    }

    @Operation(summary = "Получить опубликованные посты проекта")
    @GetMapping("/project/{projectId}")
    public List<PostDto> getPublishedPostsByProject(@PathVariable long projectId) {
        return postService.getPublishedPostsByProject(projectId);
    }
}