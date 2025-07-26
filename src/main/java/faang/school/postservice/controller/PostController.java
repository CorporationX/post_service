package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostCreateDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.PostFilterDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.service.post.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * PostController — описание класса.
 * <p>
 * TODO: описать, какие обязанности у класса.
 * </p>
 *
 * @author Myrza
 * @since 24.07.2025
 */
@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService service;

    @PostMapping()
    public ResponseEntity<PostDto> create(@Valid @RequestBody PostCreateDto createDto) {
        var post = service.create(createDto);
        return ResponseEntity.ok(post);
    }

    @PostMapping("/{postId}/publish")
    public ResponseEntity<Void> publish(@PathVariable Long postId) {
        service.publish(postId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostDto> update(@PathVariable Long postId, @Valid @RequestBody PostUpdateDto updateDto) {
        var post = service.update(postId, updateDto);
        return ResponseEntity.ok(post);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> delete(@PathVariable Long postId) {
        service.delete(postId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostDto> getById(@Valid @PathVariable Long postId) {
        var post = service.getById(postId);
        return ResponseEntity.ok(post);
    }

    @GetMapping("/search")
    public ResponseEntity<List<PostDto>> getList(@Valid @ModelAttribute PostFilterDto filterDto) {
        var posts = service.getList(filterDto);
        return ResponseEntity.ok(posts);
    }
}
