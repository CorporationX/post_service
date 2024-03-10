package faang.school.postservice.controller;

import faang.school.postservice.dto.PostDto;
import faang.school.postservice.dto.event.LikeEventDto;
import faang.school.postservice.publisher.LikeEventPublisher;
import faang.school.postservice.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "PostController", description = "Посылаем запросы в PostController")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostService postService;
    private final LikeEventPublisher likeEventPublisher;

    @Operation(
            summary = "Создаём draft post",
            description = "Получает PostDto и создаёт draft post"
    )
    @PostMapping
    public PostDto crateDraftPost(@RequestBody @Validated PostDto postDto) {
        return postService.createDraftPost(postDto);
    }

    @Operation(
            summary = "Публикуем draft post",
            description = "Получает id и публикуем post"
    )
    @PutMapping("/{id}")
    public PostDto publishPost(@PathVariable Long id) {
        return postService.publishPost(id);
    }

    @Operation(
            summary = "Обновляем draft post",
            description = "Получает PostDto и обновляет post"
    )
    @PutMapping
    public PostDto updatePost(@RequestBody @Validated PostDto postDto) {
        return postService.updatePost(postDto);
    }

    @PutMapping("/{id}/like")   //<-------- Mock для теста
    public LikeEventDto likePost(@PathVariable Long id) {
        LikeEventDto testEvent = new LikeEventDto();
        testEvent.setPostId(1L);
        testEvent.setAuthorId(1L);
        testEvent.setUserId(33L);
        likeEventPublisher.publish(testEvent);
        return testEvent;
    }
}