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
import faang.school.postservice.validator.CommentValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final PostService postService;
    private final CommentMapper commentMapper;
    private final PostMapper postMapper;
    private final CommentValidator validator;
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

    private PostDto getPost(CommentDto commentDto) {
        return postService.getPost(commentDto.getPostId());
    }

    private UserDto getAuthor(CommentDto commentDto) {
        UserDto user = userClient.getUser(commentDto.getAuthorId());
        validator.existsAuthor(user);
        return user;
    }

    private CommentDto setValues(CommentDto commentDto, UserDto userDto, PostDto postDto) {
        commentDto.setAuthorName(userDto.getUsername());
        commentDto.setPostId(postDto.getId());
        return commentDto;
    }

    @Override
    public CommentDto updateComment(CommentDto commentDto) {
        Comment comment = commentRepository.findById(commentDto.getId()).orElseThrow();

        validator.validateAuthorIdUpdateComment(commentDto);
        validator.validatePostIdUpdateComment(commentDto);
        validator.validateCommentIdUpdateComment(commentDto);
        validator.validateAuthorNameUpdateComment(commentDto);

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
        validator.validateAuthorDeleteComment(commentMapper.toDto(comment));

        commentRepository.delete(comment);
    }
}
