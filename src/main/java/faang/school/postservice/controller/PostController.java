package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/v1/posts")
public class PostController {

    @PostMapping("/")
    public ResponseEntity<?> createPost(@Valid @RequestBody PostDto postDto) {
        log.info("Был вызван запрос на создание поста {}", postDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{postId}")
    public void getPost(@PathVariable("postId")
                        @NotNull(message = "Необходимо указать АйДи поста")
                        @Min(value = 0, message = "АйДи поста должен быть положительным.")
                        long id) {
        log.info("Был вызван запрос на получение поста по его АйДи {}", id);
    }
}
