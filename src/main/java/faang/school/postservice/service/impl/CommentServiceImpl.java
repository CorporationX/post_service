package faang.school.postservice.service.impl;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.dto.CommentDto;
import faang.school.postservice.model.entity.Comment;
import faang.school.postservice.model.entity.Post;
import faang.school.postservice.model.event.CommentEvent;
import faang.school.postservice.publisher.CommentEventPublisher;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.CommentService;
import faang.school.postservice.validator.comment.CommentServiceValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final CommentServiceValidator validator;
    private final CommentMapper mapper;
    private final UserServiceClient userServiceClient;
    private final CommentEventPublisher commentEventPublisher;

    @Override
    @Transactional
    public CommentDto createComment(CommentDto commentDto, Long userId) {
        validator.validatePostExist(commentDto.getPostId());
        validator.validateCommentContent(commentDto.getContent());
        Comment comment = mapper.mapToComment(commentDto);
        CommentDto savedCommentDto = mapper.mapToCommentDto(commentRepository.save(comment));
        commentEventPublisher.publish(createCommentEvent(savedCommentDto));
        return savedCommentDto;
    }

    @Override
    public List<CommentDto> getComment(Long postId) {
        List<Comment> comments = commentRepository.findAllByPostId(postId);
        List<Comment> commentsSorted = comments.stream()
                .sorted(Comparator.comparing(Comment::getUpdatedAt).reversed())
                .toList();
        return mapper.mapToCommentDto(commentsSorted);
    }

    @Override
    public void deleteComment(Long commentId) {
        validator.validateCommentExist(commentId);
        commentRepository.deleteById(commentId);
    }

    @Override
    public CommentDto updateComment(Long commentId, CommentDto commentDto, Long userId) {
        validator.validateCommentExist(commentId);
        validator.validateCommentContent(commentDto.getContent());
        userServiceClient.getUser(userId);
        Comment comment = commentRepository.findById(commentId).orElseThrow(NoSuchElementException::new);
        comment.setContent(commentDto.getContent());
        return mapper.mapToCommentDto(commentRepository.save(comment));
    }

    private CommentEvent createCommentEvent(CommentDto savedComment) {
        Long postId = savedComment.getPostId();
        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalPost.isEmpty()) {
            throw new IllegalArgumentException("Post not found");
        }
        Post post = optionalPost.get();
        Long postAuthorId = post.getAuthorId();
        Long authorId = savedComment.getAuthorId();
        String postText = savedComment.getContent();
        Long commentId = savedComment.getId();
        return new CommentEvent(authorId, postAuthorId, postId, postText, commentId);
    }
}
