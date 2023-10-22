package faang.school.postservice.controller;

import faang.school.postservice.service.CommentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Comment Controller")
@RequestMapping("/api/v1/comments")
public class CommentController {

    private final CommentService commentService;

}
