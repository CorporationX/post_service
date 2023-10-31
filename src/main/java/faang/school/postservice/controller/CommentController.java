package faang.school.postservice.controller;

import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comment")
@Slf4j
public class CommentController {

    private final CommentService service;

    @PostMapping("/create")
    public CommentDto createComment(@RequestBody @Validated CommentDto commentDto) {
        log.info("Received request to create comment with author ID: {}, and post ID: {}", commentDto.getAuthorId(), commentDto.getPostId());
        return service.createComment(commentDto);
    }

}
