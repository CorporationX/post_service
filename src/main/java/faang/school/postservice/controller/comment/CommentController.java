package faang.school.postservice.controller.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.exception.EntityWrongParameterException;
import faang.school.postservice.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/{postId}")
    public CommentDto createComment(@PathVariable long postId,@RequestBody CommentDto commentDto){
        validateCommentDto(commentDto);
        commentDto.setPostId(postId);
        return commentService.createComment(commentDto);
    }

    @PutMapping("/{commentId}")
    public CommentDto updateComment(@PathVariable long commentId,@RequestBody CommentDto commentDto){
        validateCommentDto(commentDto);
        return commentService.updateComment(commentId, commentDto);
    }

    @GetMapping("/{postId}")
    public List<CommentDto> getAllCommentsForPost(@PathVariable long postId){
        return commentService.getAllCommentsForPost(postId);
    }

    @DeleteMapping("/{commentId}")
    public void deleteComment(@PathVariable long commentId,@RequestHeader("userId") long userId){
        commentService.deleteComment(commentId, userId);
    }

    private void validateCommentDto(CommentDto commentDto) {
        if (commentDto.getContent() == null || commentDto.getContent().isEmpty() || commentDto.getContent().length() > 4096) {
            throw new EntityWrongParameterException("Содержание комментария должно быть предоставлено и не может быть пустым или содержать более 4096 символов");
        }
        if (commentDto.getAuthorId() == null) {
            throw new EntityWrongParameterException("Необходимо указать id автора");
        }
        if (commentDto.getPostId() == null) {
            throw new EntityWrongParameterException("Необходимо указать id сообщения");
        }
    }
}
