package faang.school.postservice.controller.comment;

import static faang.school.postservice.contants.InfoMessage.*;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CommentUpdateDto;
import faang.school.postservice.service.comment.CommentService;
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

@Slf4j
@RestController
@RequestMapping("/v1/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    public void createComment(@RequestBody CommentDto commentDto) {
        log.info(INFO_START_CONTROLLER_CREATE_COMMENT, commentDto.getAuthorId(), commentDto.getPostId());
        commentService.createComment(commentDto);
    }

    @PutMapping("/{id}")
    public void updateComment(@PathVariable Long id, @RequestBody CommentUpdateDto commentUpdateDto) {
        log.info(INFO_START_CONTROLLER_UPDATE_COMMENT, id, commentUpdateDto.getAuthorId());
        commentService.updateComment(id, commentUpdateDto);
    }

    @GetMapping
    public List<CommentDto> getComments(@RequestParam Long postId) {
        log.info(INFO_START_CONTROLLER_GET_COMMENT, postId);
        return commentService.getCommentsByPostId(postId);
    }

    @DeleteMapping("/{id}")
    public void deleteComment(@PathVariable Long id) {
        log.info(INFO_START_CONTROLLER_DELETE_COMMENT, id);
        commentService.deleteComment(id);
    }
}
