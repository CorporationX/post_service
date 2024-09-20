package faang.school.postservice.service.impl;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.mapper.post.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.CommentService;
import faang.school.postservice.validator.CommentValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final CommentMapper commentMapper;
    private final CommentValidator validator;

    @Override
    public CommentDto createComment(CommentDto commentDto) {
        validator.existsAuthor(commentDto);
        Post post = postRepository.findById(commentDto.getPostId()).orElseThrow();
        Comment newComment = commentMapper.toEntity(commentDto);
        newComment.setPost(post);
        var comment = commentRepository.save(newComment);
        return commentMapper.toDto(comment);
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
                .sorted(Comparator.comparing(Comment::getCreatedAt))
                .map(commentMapper::toDto)
                .toList();
    }

    @Override
    public void deleteComment(long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow();
        validator.validateDeleteComment(commentMapper.toDto(comment));

        commentRepository.delete(comment);
    }
}
