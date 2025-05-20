package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostCreateRequestDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.facade.PostFacade;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
@Slf4j
public class PostController {
    private final PostFacade postFacade;
    @PostMapping("/draft")
    public ResponseEntity<PostResponseDto> createDraftPost(@RequestBody @Valid PostCreateRequestDto postCreateRequestDto) {
        log.info("Post controller accepted request create post {}", postCreateRequestDto);

        PostResponseDto response = postFacade.createDraftPost(postCreateRequestDto);
        log.info("Post controller return response create post {}", response);
        return new ResponseEntity<>(response, HttpStatus.CREATED) ;
    }
}
