package faang.school.postservice.service.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.validator.CommentValidator;
import faang.school.postservice.validator.PostValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostValidator postValidator;
    private final UserServiceClient userServiceClient;
    private final CommentMapper commentMapper;
    private final CommentValidator commentValidator;
    private final ImageService imageService;

    public CommentDto createComment(Long postId, CommentDto commentDto) {
        postValidator.getPostById(postId);
        validateUserId(commentDto.getAuthorId());

        Comment comment = commentMapper.toComment(commentDto);
        comment = commentRepository.save(comment);
        comment = saveCommentWithImage(commentDto, comment);

        return commentMapper.toCommentDto(comment);
    }

    public CommentDto updateComment(Long commentId, CommentDto commentDto) {
        commentValidator.validateCommentDto(commentDto);
        Comment comment = commentRepository
                .findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment with ID " + commentId + " not found"));

        comment.setContent(commentDto.getContent());
        comment.setUpdatedAt(LocalDateTime.now());

        return commentMapper.toCommentDto((commentRepository.save(comment)));
    }

    public List<CommentDto> getAllComments(Long postId) {
        commentValidator.validateListComments(postId);

        List<Comment> comments = commentRepository.findAllByPostId(postId);

        return comments.stream()
                .sorted(Comparator.comparing(Comment::getCreatedAt).reversed())
                .map(commentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with ID" + commentId));

        imageService.deleteImageIfExists(comment.getLargeImageFileKey());
        imageService.deleteImageIfExists(comment.getSmallImageFileKey());

        commentRepository.deleteById(commentId);
    }

    private void validateUserId(long userId) {
        userServiceClient.getUser(userId);
    }

    private Comment saveCommentWithImage(CommentDto commentDto, Comment comment) {
        MultipartFile image = commentDto.getImage();
        if (image != null && !image.isEmpty()) {
            ImageService.ImageKeys keys = imageService.uploadResizedImages(image, comment.getId());

            comment.setLargeImageFileKey(keys.largeKey());
            comment.setSmallImageFileKey(keys.smallKey());

            comment = commentRepository.save(comment);
        }
        return comment;
    }
}
