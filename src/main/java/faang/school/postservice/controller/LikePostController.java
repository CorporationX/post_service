package faang.school.postservice.controller;


import faang.school.postservice.dto.like.LikePostCreateDto;
import faang.school.postservice.service.like.LikePostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/likes")
public class LikePostController {

    private final LikePostService likePostService;

    @PostMapping()
    public ResponseEntity<LikePostCreateDto> likePost(@Valid @RequestBody LikePostCreateDto likePostCreateDto) {
        log.info("Received request to like post with ID: {}", likePostCreateDto.getPostId());
        return ResponseEntity.ok(likePostService.likePost(likePostCreateDto));
    }
}
