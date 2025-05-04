package faang.school.postservice.controller.comment;

import faang.school.postservice.dto.comment.CommentRequestDto;
import faang.school.postservice.dto.comment.CommentResponseDto;
import faang.school.postservice.dto.comment.CommentUpdateDto;
import faang.school.postservice.service.comment.CommentService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static faang.school.postservice.contants.InfoMessage.INFO_START_CONTROLLER_CREATE_COMMENT;
import static faang.school.postservice.contants.InfoMessage.INFO_START_CONTROLLER_DELETE_COMMENT;
import static faang.school.postservice.contants.InfoMessage.INFO_START_CONTROLLER_GET_COMMENT;
import static faang.school.postservice.contants.InfoMessage.INFO_START_CONTROLLER_UPDATE_COMMENT;

@Slf4j
@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    public void createComment(@NonNull @RequestBody CommentRequestDto commentRequestDto) {
        log.info(INFO_START_CONTROLLER_CREATE_COMMENT, commentRequestDto.getAuthorId(), commentRequestDto.getPostId());
        commentService.createComment(commentRequestDto);
    }

    @PutMapping("/{id}")
    public void updateComment(@PathVariable Long id, @NonNull @RequestBody CommentUpdateDto commentUpdateDto) {
        log.info(INFO_START_CONTROLLER_UPDATE_COMMENT, id, commentUpdateDto.getAuthorId());
        commentService.updateComment(id, commentUpdateDto);
    }

    @GetMapping("/by-post")
    public List<CommentResponseDto> getComments(@RequestParam Long postId) {
        log.info(INFO_START_CONTROLLER_GET_COMMENT, postId);
        return commentService.getCommentsByPostId(postId);
    }

    @DeleteMapping("/{id}")
    public void deleteComment(@PathVariable Long id) {
        log.info(INFO_START_CONTROLLER_DELETE_COMMENT, id);
        commentService.deleteComment(id);
    }
}
