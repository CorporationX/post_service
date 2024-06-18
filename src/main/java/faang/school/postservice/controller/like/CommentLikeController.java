package faang.school.postservice.controller.like;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.service.like.CommentLikeService;
import faang.school.postservice.validation.like.comment.CommentLikeAnnotationValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("comment/like")
@RequiredArgsConstructor
@Validated(CommentLikeAnnotationValidator.class)
public class CommentLikeController {
    private final CommentLikeService commentLikeService;
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LikeDto likeComment(@Valid @RequestBody LikeDto dto) {
        return commentLikeService.likeComment(dto);
    }
    
    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public void deleteCommentLike(@Valid @RequestBody LikeDto dto) {
        commentLikeService.deleteCommentLike(dto);
    }
}
