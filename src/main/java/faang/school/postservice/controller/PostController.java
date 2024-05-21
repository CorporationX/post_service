package faang.school.postservice.controller;

import faang.school.postservice.dto.PostDto;
import faang.school.postservice.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("${endpoint.context-path.post}")
public class PostController {

    private final PostService postService;

    @PostMapping("/create")
    @Operation(
            summary = "Creating a draft post",
            description = "Exactly one author user or project creates a draft of the post"
    )
    public PostDto createPost() {

    }

}