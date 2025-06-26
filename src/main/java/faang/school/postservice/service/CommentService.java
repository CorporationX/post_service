package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.exception.CommentNotFoundException;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.util.Utils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    public static final String COMMENT_BY_ID_NOT_FOUND = "Comment by id [{}] not found";
    public static final String POST_BY_ID_NOT_FOUND = "Post by id [{}] not found";

    private final CommentRepository commentRepository;
    private final Utils utils;
    private final CommentMapper commentMapper;
    private final UserServiceClient userServiceClient;
    private final PostRepository postRepository;
    private final PostService postService;

    /**
     * @return возвращает Comment по его id
     */
    public Comment findCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() ->
                        new CommentNotFoundException(utils.format(COMMENT_BY_ID_NOT_FOUND, commentId)));
    }

    @Transactional
    public CommentDto addComment(CommentDto commentDto) {
        Comment comment = commentMapper.toEntity(commentDto);
        Post post = postService.findPostById(commentDto.postId());
        comment.setPost(post);
        comment.setCreatedAt(LocalDateTime.now());
        comment = commentRepository.save(comment);
        return commentMapper.toDto(comment);
    }

    @Transactional
    public CommentDto updateComment(CommentDto commentDto) {
        Comment comment = findCommentById(commentDto.id());
        comment.setContent(commentDto.content());
        comment = commentRepository.save(comment);
        return commentMapper.toDto(comment);
    }

    @Transactional
    public List<CommentDto> getAllComments(Long postId) {
        List<Comment> comments = commentRepository.findAllByPostId(postId);
        return comments.stream()
                .sorted((c1, c2) -> c2.getCreatedAt().compareTo(c1.getCreatedAt()))
                .map(commentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteComment(Long commentId) {
        Comment comment = findCommentById(commentId);
        commentRepository.delete(comment);
    }
}