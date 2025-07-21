package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostDraftDto;
import faang.school.postservice.integration.user.dto.UserResponseDto;
import faang.school.postservice.integration.user.service.UserServiceClient;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;


    @PostMapping("/draft")
    public ResponseEntity<Void> createDraft(@RequestBody @Validated PostDraftDto postDraftDto) {
        postService.createPostDraft(postDraftDto);
        return ResponseEntity.ok().build();
    }
}
