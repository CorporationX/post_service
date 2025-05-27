package faang.school.postservice.controller.comment;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CommentForCreationDto;
import faang.school.postservice.dto.comment.CommentForUpdateDto;
import faang.school.postservice.dto.comment.CommentOutputDto;
import faang.school.postservice.service.CommentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService service;
    private final UserContext userContext;

    @PostMapping
    public CommentOutputDto create(@Valid @RequestBody CommentForCreationDto commentDto) {
        log.info("Creating a comment by user {} for post with id {} - Started"
                , userContext.getUserId(), commentDto.getPostId());
        return service.create(commentDto);
    }

    @PatchMapping
    public CommentDto update(@Valid @RequestBody CommentForUpdateDto commentDto) {
        log.info("Update a comment with id {} by user {} - Started"
                , commentDto.getId(), userContext.getUserId());
        return service.update(commentDto);
    }

    @GetMapping("/post/{postId}")
    public List<CommentOutputDto> findByPostId(@NotNull @PathVariable long postId) {
        log.info("Searching for a list of comments for post with id {} by user {} - Started"
                , postId, userContext.getUserId());
        return service.findByPostId(postId);
    }

    @GetMapping("/{commentId}")
    public CommentDto findById(@NotNull @PathVariable long commentId) {
        log.info("Searching for a comment with id {} by user {} - Started"
                , commentId, userContext.getUserId());
        return service.findById(commentId);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteById(@NotNull @PathVariable long commentId) {
        log.info("Deleting a comment with id {} by user {} - Started"
                , commentId, userContext.getUserId());
        service.deleteById(commentId);
        return ResponseEntity.noContent().build();
    }
}
