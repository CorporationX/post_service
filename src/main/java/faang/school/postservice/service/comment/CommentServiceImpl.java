package faang.school.postservice.service.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CommentForCreationDto;
import faang.school.postservice.dto.comment.CommentForUpdateDto;
import faang.school.postservice.dto.comment.CommentOutputDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.CommentDtoStatus;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.CommentService;
import faang.school.postservice.service.PostService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private PostService postService;
    @Autowired
    private UserServiceClient userServiceClient;
    @Autowired
    private UserContext userContext;

    @Override
    public CommentOutputDto create(CommentForCreationDto commentDto) {
        Post post = postService.findPostById(commentDto.getPostId());
        long userId = userContext.getUserId();
        userServiceClient.getUser(userId);
        Comment comment = commentMapper.toEntity(commentDto);
        comment.setAuthorId(userId);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setPost(post);
        return commentMapper.toDto(commentRepository.save(comment));
    }

    @Override
    public CommentOutputDto update(CommentForUpdateDto commentDto) {
        Comment existingComment = findCommentById(commentDto.getId());
        validateCommentAuthor(existingComment);
        Comment comment = commentMapper.updateEntityFromDto(commentDto, existingComment);
        comment.setUpdatedAt(LocalDateTime.now());
        comment = commentRepository.save(comment);
        return commentMapper.toDto(comment);
    }

    private void validateCommentAuthor(Comment existingComment) {
        long userId = userContext.getUserId();
        userServiceClient.getUser(userId);
        if(existingComment.getAuthorId() != userId){
            throw new DataValidationException("Comment can be changed only by their authors");
        }
    }

    @Override
    public CommentOutputDto findById (long commentId){
        return commentMapper.toDto(findCommentById(commentId));
    }

    @Override
    public void deleteById(long commentId) {
        Comment comment = findCommentById(commentId);
        commentRepository.delete(comment);
    }

    @Override
    public List<CommentOutputDto> findByPostId(long postId) {
        return commentMapper.toListDto(commentRepository.findAllByPostId(postId).stream()
                .sorted(Comparator.comparing(Comment::getCreatedAt))
                .toList());
    }

    private Comment findCommentById(long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException
                        (String.format("There is no comment with id %d", commentId)));
    }
}
