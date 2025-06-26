package faang.school.postservice.controller.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CommentPostRequestDto;
import faang.school.postservice.dto.comment.CommentPostResponseDto;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.service.CommentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
@Validated
public class CommentController {

    private final CommentService commentService;
    private final CommentMapper commentMapper;

    @PostMapping("/{postId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentPostResponseDto addComment(
            @PathVariable @NotNull @Positive(message = "PostId must be positive") Long postId,
            @Valid @RequestBody CommentPostRequestDto commentPostRequestDto) {
        log.debug("add comment request: postId={}, request={}", postId, commentPostRequestDto);
        CommentDto commentDto = commentMapper.toDto(commentPostRequestDto);
        CommentDto commentDtoWithPostId = new CommentDto(
                null,
                commentDto.content(),
                commentDto.authorId(),
                postId
        );
        CommentDto resultCommentDto = commentService.addComment(commentDtoWithPostId);
        return commentMapper.toPostResponseDto(resultCommentDto);
    }

    @PutMapping("/{postId}/{commentId}")
    public ResponseEntity<CommentPostResponseDto> updateComment(
            @PathVariable("postId") @NotNull @Positive(message = "PostId must be positive") Long postId,
            @PathVariable("commentId") @NotNull @Positive(message = "CommentId must be positive") Long commentId,
            @Valid @RequestBody CommentPostRequestDto commentPostRequestDto) {
        log.debug("update comment request: postId={}, commentId={}, request={}", postId, commentId,
                commentPostRequestDto);

        CommentDto commentDto = commentMapper.toDto(commentPostRequestDto);
        CommentDto commentDtoWithId = new CommentDto(
                commentId,
                commentDto.content(),
                commentDto.authorId(),
                postId
        );
        CommentDto updatedCommentDto = commentService.updateComment(commentDtoWithId);
        return ResponseEntity.ok(commentMapper.toPostResponseDto(updatedCommentDto));
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<CommentPostResponseDto>> getAllComments(
            @PathVariable("postId") @NotNull @Positive(message = "PostId must be positive") Long postId) {
        log.debug("get all comments for post request: postId={}", postId);
        List<CommentDto> commentDtos = commentService.getAllComments(postId);
        List<CommentPostResponseDto> commentPostResponseDtos = commentDtos.stream()
                .map(commentMapper::toPostResponseDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(commentPostResponseDtos);
    }

    @DeleteMapping("/{postId}/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(
            @PathVariable("postId") @NotNull @Positive(message = "PostId must be positive") Long postId,
            @PathVariable("commentId") @NotNull @Positive(message = "CommentId must be positive") Long commentId) {
        log.debug("delete comment request: commentId={}", commentId);
        commentService.deleteComment(commentId);
    }
}