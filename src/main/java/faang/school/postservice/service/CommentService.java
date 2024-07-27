package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.UpdatedCommentDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.CommentException;
import faang.school.postservice.exception.PostException;
import faang.school.postservice.exception.UserException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;
    private final CommentMapper commentMapper;

    public CommentDto createComment(CommentDto commentDto) {
        validatedCommentAuthorId(commentDto);

        Comment comment = commentMapper.toEntity(commentDto);
        return commentMapper.toDto(commentRepository.save(comment));
    }

    private void validatedCommentAuthorId(CommentDto commentDto) {
        Long authorId = commentDto.getAuthorId();
        UserDto commentAuthor = userServiceClient.getUser(authorId);
        if (commentAuthor == null) {
            throw new UserException("User with ID = " + authorId + " does not found");
        }
    }

    public CommentDto updateComment(UpdatedCommentDto updatedCommentDto) {
        Comment updatedComment = commentRepository.findById(updatedCommentDto.getId())
                .orElseThrow(() -> new CommentException("Comment with ID = " + updatedCommentDto.getId() + " does not found"));
        updatedComment.setContent(updatedCommentDto.getContent());

        return commentMapper.toDto(commentRepository.save(updatedComment));
    }

    public List<CommentDto> getAllCommentsByPostIdSortedByCreatedDate(Long postId) {
        if(!postRepository.existsById(postId)) {
            throw new PostException("Post with ID = " + postId + "does not exist");
        }
        List<Comment> postsComments = commentRepository
                .findAllByPostId(postId)
                .stream()
                .sorted(Comparator.comparing(Comment::getCreatedAt))
                .toList();
        return commentMapper.toDtoList(postsComments);
    }

    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }
}
