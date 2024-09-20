package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
@Slf4j
public class PostController {

    private final PostService postService;

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<PostDto>> getPostsByProjectId(@PathVariable long projectId) {
        log.info("Получение постов для проекта с ID: {}", projectId);
        List<PostDto> posts = postService.getPostsByProjectId(projectId);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/author/{userId}")
    public ResponseEntity<List<PostDto>> getPostsByAuthorId(@PathVariable long userId) {
        log.info("Получение постов для пользователя с ID: {}", userId);
        List<PostDto> posts = postService.getPostsByAuthorId(userId);
        return ResponseEntity.ok(posts);
    }
}