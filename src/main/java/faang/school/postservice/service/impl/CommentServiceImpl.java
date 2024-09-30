package faang.school.postservice.service.impl;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.mapper.post.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.CommentService;
import faang.school.postservice.service.PostService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final PostService postService;
    private final CommentMapper commentMapper;
    private final PostMapper postMapper;
    private final UserServiceClient userClient;

    @Override
    public CommentDto createComment(CommentDto commentDto) {
        UserDto user = getAuthor(commentDto);
        PostDto postDto = getPost(commentDto);

        Comment newComment = commentMapper.toEntity(commentDto);
        newComment.setPost(postMapper.toEntity(postDto));

        Comment comment = commentRepository.save(newComment);
        CommentDto dto = commentMapper.toDto(comment);

        return setValues(dto, user, postDto);
    }

    @Override
    public CommentDto updateComment(CommentDto commentDto) {
        Comment comment = commentRepository.findById(commentDto.getId())
                .orElseThrow(() ->
                        new EntityNotFoundException("There is no comment with ID " + commentDto.getId()));

        validateAuthorIdUpdateComment(commentDto);
        validatePostIdUpdateComment(commentDto);
        validateCommentIdUpdateComment(commentDto);
        validateAuthorNameUpdateComment(commentDto);

        comment.setContent(commentDto.getContent());
        return commentMapper.toDto(commentRepository.save(comment));
    }

    @Override
    public List<CommentDto> getAllCommentsByPostId(long postId) {
        List<Comment> commentsDto = commentRepository.findAllByPostId(postId);
        return commentsDto.stream()
                .sorted(Comparator.comparing(Comment::getCreatedAt).reversed())
                .map(comment -> {
                    CommentDto dto = commentMapper.toDto(comment);
                    dto.setPostId(postId);
                    UserDto author = userClient.getUser(comment.getAuthorId());
                    if (author != null) {
                        dto.setAuthorName(author.getUsername());
                    }
                    return dto;
                })
                .toList();
    }

    @Override
    public void deleteComment(long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow();
        validateAuthorDeleteComment(commentMapper.toDto(comment), comment);

        commentRepository.delete(comment);
    }

    private PostDto getPost(CommentDto commentDto) {
        return postService.getPost(commentDto.getPostId());
    }

    private UserDto getAuthor(CommentDto commentDto) {
        UserDto user = userClient.getUser(commentDto.getAuthorId());

        if (user == null) {
            throw new DataValidationException(String.format("Validation failed for comment with ID: %d", commentDto.getId()));
        }

        return user;
    }

    private CommentDto setValues(CommentDto commentDto, UserDto userDto, PostDto postDto) {
        commentDto.setAuthorName(userDto.getUsername());
        commentDto.setPostId(postDto.getId());
        return commentDto;
    }

    private Comment getComment(CommentDto commentDto) {
        return commentRepository.findById(commentDto.getId()).orElseThrow();
    }

    private void validateAuthorIdUpdateComment(CommentDto commentDto) {
        if (!(commentDto.getAuthorId() == getComment(commentDto).getAuthorId())) {
            throw new DataValidationException("The request author ID and the comment author ID do not match");
        }
    }

    private void validatePostIdUpdateComment(CommentDto commentDto) {
        if (!(commentDto.getPostId() == getComment(commentDto).getPost().getId())) {
            throw new DataValidationException("The request post id and the comment post id do not match");
        }
    }

    private void validateAuthorNameUpdateComment(CommentDto commentDto) {
        if (!(commentDto.getAuthorName().equals(userClient.getUser(commentDto.getAuthorId()).getUsername()))) {
            throw new DataValidationException(String.format("%s are not the author of the comment ", commentDto.getAuthorName()));
        }
    }

    private void validateCommentIdUpdateComment(CommentDto commentDto) {
        if (!(commentDto.getId().equals(getComment(commentDto).getId()))) {
            throw new DataValidationException("Request comment id does not match comment id");
        }
    }

    private void validateAuthorDeleteComment(CommentDto commentDto, Comment comment) {
        if (!(comment.getAuthorId() == commentDto.getAuthorId())) {
            throw new DataValidationException(String.format("%d this user cannot delete a comment. ", commentDto.getAuthorId()));
        }
    }
}
