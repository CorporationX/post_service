package faang.school.postservice.service.impl;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.mapper.post.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.CommentService;
import faang.school.postservice.service.PostService;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

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
                .orElseThrow(() -> new NoSuchElementException("Comment not found"));

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
            throw new ValidationException("Author name is required");
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
            throw new ValidationException("Author name can't be changed");
        }
    }

    private void validatePostIdUpdateComment(CommentDto commentDto) {
        if (!(commentDto.getPostId() == getComment(commentDto).getPost().getId())) {
            throw new ValidationException("Post id can't be changed");
        }
    }

    private void validateAuthorNameUpdateComment(CommentDto commentDto) {
        if (!(commentDto.getAuthorName().equals(userClient.getUser(commentDto.getAuthorId()).getUsername()))) {
            throw new ValidationException("Author name can't be changed");
        }
    }

    private void validateCommentIdUpdateComment(CommentDto commentDto) {
        if (!(commentDto.getId().equals(getComment(commentDto).getId()))) {
            throw new ValidationException("Comment id can't be changed");
        }
    }

    private void validateAuthorDeleteComment(CommentDto commentDto, Comment comment) {
        if (!(comment.getAuthorId() == commentDto.getAuthorId())) {
            throw new ValidationException("Comment can't be deleted by this user");
        }
    }
}
