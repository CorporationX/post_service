package faang.school.postservice.controller.comment;

import faang.school.postservice.dto.comment.CommentCreateDto;
import faang.school.postservice.dto.comment.CommentDtoResponse;
import faang.school.postservice.dto.comment.CommentResponseImageDto;
import faang.school.postservice.dto.comment.CommentUpdateDto;
import faang.school.postservice.service.comment.CommentMappingFacade;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comments")
public class CommentControllerImpl implements CommentController {
    private final CommentMappingFacade commentServiceF;

    @PostMapping("/post/{postId}")
    @Override
    public ResponseEntity<CommentDtoResponse> createComment(@Valid @RequestBody CommentCreateDto commentDto) {
        long postId = commentDto.getPostId();
        String content = commentDto.getContent();

        CommentDtoResponse commentDtoResponse = commentServiceF.createComment(postId, content);
        return ResponseEntity.ok(commentDtoResponse);
    }

    @PatchMapping("/{commentId}")
    @Override
    public ResponseEntity<CommentDtoResponse> updateComment(@Valid @RequestBody CommentUpdateDto commentDto) {
        long commentId = commentDto.getCommentId();
        String newContent = commentDto.getNewContent();

        CommentDtoResponse commentDtoResponse = commentServiceF.updateComment(commentId, newContent);
        return ResponseEntity.ok(commentDtoResponse);
    }

    @GetMapping("/{postId}")
    @Override
    public ResponseEntity<List<CommentDtoResponse>> getAllComments(@PathVariable long postId) {
        List<CommentDtoResponse> commentDtoResponseList = commentServiceF.getAllComments(postId);
        return ResponseEntity.ok(commentDtoResponseList);
    }

    @DeleteMapping("/{commentId}")
    @Override
    public ResponseEntity<Long> deleteComment(@PathVariable long commentId) {
        commentServiceF.deleteComment(commentId);
        return ResponseEntity.ok(commentId);
    }

    @PostMapping("/{commentId}/image")
    @Override
    public ResponseEntity<String> uploadFile(@PathVariable long commentId,
                                             @RequestParam MultipartFile file) {
        commentServiceF.uploadFile(commentId, file);
        return ResponseEntity.ok("File uploaded: %s".formatted(file.getOriginalFilename()));
    }

    @DeleteMapping("/{commentId}/image")
    @Override
    public ResponseEntity<String> deleteFile(@PathVariable long commentId) {
        commentServiceF.deleteFile(commentId);
        return ResponseEntity.ok("File deleted");
    }

    @GetMapping("/{commentId}/image/small")
    @Override
    public ResponseEntity<Resource> getSmallImage(@PathVariable long commentId) {
        CommentResponseImageDto imageDto = commentServiceF.getSmallImage(commentId);

        return ResponseEntity.ok()
                .contentLength(imageDto.getContentLength())
                .contentType(MediaType.parseMediaType(imageDto.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachement; filename=" + imageDto.getFileName())
                .body(imageDto.getResource());
    }

    @GetMapping("/{commentId}/image/large")
    @Override
    public ResponseEntity<Resource> getLargeImage(@PathVariable long commentId) {
        CommentResponseImageDto imageDto = commentServiceF.getLargeImage(commentId);

        return ResponseEntity.ok()
                .contentLength(imageDto.getContentLength())
                .contentType(MediaType.parseMediaType(imageDto.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachement; filename=" + imageDto.getFileName())
                .body(imageDto.getResource());
    }
}
