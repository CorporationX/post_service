package faang.school.postservice.controller.comment;

import faang.school.postservice.controller.ApiPath;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.service.comment.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiPath.COMMENT)
public class CommentController {
    private final CommentService commentService;
    // postman проверено
    @PostMapping("/post/{id}")
    public ResponseEntity<CommentDto> addCommentController(@PathVariable Long id,
                                                           @RequestBody @Valid CommentDto commentDto) {
        return ResponseEntity.ok(commentService.addCommentService(id, commentDto));
    }
    // postman проверено
    @PutMapping("/update/{id}")
    public ResponseEntity<CommentDto> updateCommentController(@PathVariable Long id,
                                                              @RequestBody @Valid CommentDto commentDto) {
        return ResponseEntity.ok(commentService.updateCommentService(id, commentDto));
    }
    // postman проверено
    @GetMapping("/get/{id}")
    public ResponseEntity<List<CommentDto>> getCommentsController(@PathVariable Long id) {
        return ResponseEntity.ok(commentService.getCommentsService(id));
    }
    // postman проверено
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<CommentDto> deleteCommentController(@PathVariable Long id,
                                                              @RequestBody @Valid CommentDto commentDto) {
        return ResponseEntity.ok(commentService.deleteCommentService(id, commentDto));
    }

}
