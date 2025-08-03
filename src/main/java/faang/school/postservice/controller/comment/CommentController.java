package faang.school.postservice.controller.comment;

import com.fasterxml.jackson.core.JsonProcessingException;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CommentForCreationDto;
import faang.school.postservice.dto.comment.CommentForUpdateDto;
import faang.school.postservice.dto.comment.CommentOutputDto;
import faang.school.postservice.service.CommentFileService;
import faang.school.postservice.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.springframework.expression.AccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
@Tag(name = "Comment Management", description = "Operations related to comments")
public class CommentController {

    private final CommentService service;
    private final CommentFileService commentFileService;
    private final UserContext userContext;

    @PostMapping
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Created successfully")
    })
    public CommentOutputDto create(@Valid @RequestBody CommentForCreationDto commentDto) throws JsonProcessingException {
        log.info("Creating a comment by user {} for post with id {} - Started"
                , userContext.getUserId(), commentDto.getPostId());
        return service.createComment(commentDto);
    }

    @PatchMapping
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Updated successfully")
    })
    public CommentDto update(@Valid @RequestBody CommentForUpdateDto commentDto) {
        log.info("Update a comment with id {} by user {} - Started"
                , commentDto.getId(), userContext.getUserId());
        return service.updateComment(commentDto);
    }

    @GetMapping("/post/{postId}")
    @Operation(summary = "Gets comments by postID",
            description = "Post must exist")
    public List<CommentOutputDto> findByPostId(@NotNull @PathVariable long postId) {
        log.info("Searching for a list of comments for post with id {} by user {} - Started"
                , postId, userContext.getUserId());
        return service.findCommentByPostId(postId);
    }

    @GetMapping("/{commentId}")
    @Operation(summary = "Gets comment by its ID",
            description = "Comment must exist")
    public CommentDto findById(@NotNull @PathVariable long commentId) {
        log.info("Searching for a comment with id {} by user {} - Started"
                , commentId, userContext.getUserId());
        return service.findCommentById(commentId);
    }

    @DeleteMapping("/{commentId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deleted successfully")
    })
    public ResponseEntity<Void> deleteById(@NotNull @PathVariable long commentId) {
        log.info("Deleting a comment with id {} by user {} - Started"
                , commentId, userContext.getUserId());
        service.deleteCommentById(commentId);
        return ResponseEntity.noContent().build();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{commentId}/comment-image")
    public List<String> uploadCommentImage
            (@PathVariable Long commentId, @RequestParam("file") MultipartFile file)
            throws AccessException, FileSizeLimitExceededException {
        log.debug("Uploading image for comment with id {} - Started", commentId);
        return commentFileService.addImageToComment(commentId, file);
    }

    @DeleteMapping("/{commentId}/comment-image")
    public ResponseEntity<Void> deleteCommentImage(@PathVariable Long commentId) throws AccessException {
        log.debug("Deleting image for comment with id {} - Started", commentId);
        commentFileService.deleteImageFromCommentById(commentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{commentId}/comment-image")
    public ResponseEntity<byte[]> downloadCommentImage(@PathVariable Long commentId,
                                                       @RequestParam(value = "size", required = false) String size) {
        log.debug("Getting image for comment with id {} - Started", commentId);
        byte[] image;
        image = commentFileService.getCommentImage(commentId, size);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        return new ResponseEntity<>(image, headers, HttpStatus.OK);
    }
}
