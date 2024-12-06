package faang.school.postservice.controller;


import faang.school.postservice.dto.like.LikePostCreateDto;
import faang.school.postservice.service.like.LikePostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/likes")
public class LikePostController {

    private final LikePostService likePostService;

    @PostMapping()
    public ResponseEntity<LikePostCreateDto> likePost(@Valid @RequestBody LikePostCreateDto likePostCreateDto) {
        return ResponseEntity.ok(likePostService.likePost(likePostCreateDto));
    }
}
