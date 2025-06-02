package faang.school.postservice.controller.comment;

import faang.school.postservice.dto.comment.CommentDtoResponse;
import faang.school.postservice.service.comment.CommentServiceFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comments")
public class CommentControllerImpl implements CommentController {
    private final CommentServiceFacade commentServiceF;

    @Override
    public ResponseEntity<CommentDtoResponse> createComment(@PathVariable long postId,
                                                            @RequestParam String content) {
        CommentDtoResponse commentDtoResponse = commentServiceF.createComment(postId, content);
        return ResponseEntity.ok(commentDtoResponse);

        //todo сюда добавить опциональное добавление файла
    }

    @Override
    public ResponseEntity<CommentDtoResponse> updateComment(@PathVariable long commentId,
                                                            @RequestParam String newContent) {
        CommentDtoResponse commentDtoResponse = commentServiceF.updateComment(commentId, newContent);
        return ResponseEntity.ok(commentDtoResponse);
    }

    @Override
    public ResponseEntity<List<CommentDtoResponse>> getAllComments(@PathVariable long postId) {
        List<CommentDtoResponse> commentDtoResponseList = commentServiceF.getAllComments(postId);
        return ResponseEntity.ok(commentDtoResponseList);
    }

    @Override
    public ResponseEntity<Long> deleteComment(@PathVariable long commentId) {
        commentServiceF.deleteComment(commentId);
        return ResponseEntity.ok(commentId);
    }

    @Override
    public ResponseEntity<String> uploadFile(long commentId, MultipartFile file) {
        String fileName = file.getOriginalFilename();
        long fileSize = file.getSize();
        String contentType = file.getContentType();
        // todo как то надо проверить размер, либо делать это на сервисном слое
        // todo что передавать в сервис
        commentServiceF.uploadFile(commentId, file);
        return ResponseEntity.ok("File uploaded: %s".formatted(fileName));
    }
}
