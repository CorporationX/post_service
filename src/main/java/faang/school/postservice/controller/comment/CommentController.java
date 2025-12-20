package faang.school.postservice.controller.comment;

import faang.school.postservice.controller.facade.comment.CommentFacade;
import faang.school.postservice.dto.comment.CommentCreateDto;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CommentUpdateDto;
import faang.school.postservice.dto.common.PageResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
@Tag(name = "Comments", description = "Endpoints for managing post comments")
public class CommentController implements CommentApi {

    private final CommentFacade commentFacade;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createComment(@Parameter(description = "Данные для создания комментария")
                                    @Valid @RequestBody CommentCreateDto commentCreateDto) {
        return commentFacade.create(commentCreateDto);
    }

    @PatchMapping("/{commentId}")
    public CommentDto updateComment(@Parameter(description = "ID комментария", example = "123")
                                    @PathVariable Long id,
                                    @Valid @RequestBody CommentUpdateDto commentUpdateDto) {
        return commentFacade.update(id, commentUpdateDto);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@Parameter(description = "ID комментария", example = "123")
                              @PathVariable Long id) {
        commentFacade.delete(id);
    }

    @GetMapping("/{commentId}")
    public CommentDto getCommentById(@Parameter(description = "ID комментария", example = "123")
                                     @PathVariable Long id) {
        return commentFacade.getById(id);
    }

    @GetMapping
    public PageResponse<CommentDto> getCommentsByPostId(
            @RequestParam Long postId,
            Pageable pageable
    ) {
        return commentFacade.getByPostId(postId, pageable);
    }
}
