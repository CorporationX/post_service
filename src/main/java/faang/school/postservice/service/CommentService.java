package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.dto.kafka.KafkaCommentEvent;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.kafka.producer.KafkaCommentProducer;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final UserServiceClient userServiceClient;
    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final KafkaCommentProducer kafkaCommentProducer;

    @Transactional
    public CommentDto create(CommentDto commentDto, long postId) {
        validateAuthorExists(commentDto);

        Post post = getPost(postId);
        Comment comment = commentMapper.toEntity(commentDto);
        comment.setPost(post);

        commentRepository.save(comment);
        CommentDto afterSave = commentMapper.toDto(comment);

        KafkaCommentEvent kafkaEvent = createKafkaCommentEvent(comment);
        kafkaCommentProducer.publishKafkaEvent(kafkaEvent);

        return afterSave;
    }

    @Transactional
    public CommentDto update(CommentDto commentDto, long postId) {
        Comment comment = validateCommentDto(commentDto, postId);
        comment.setContent(commentDto.getContent());
        return commentMapper.toDto(commentRepository.save(comment));
    }

    @Transactional
    public CommentDto delete(CommentDto commentDto, long postId) {
        Comment comment = validateCommentDto(commentDto, postId);
        commentRepository.delete(comment);
        return commentDto;
    }

    public List<CommentDto> getAllCommentsByPostId(long postId) {
        List<Comment> commentList = commentRepository.findAllByPostId(postId);
        return commentList.stream()
                .sorted(Comparator.comparing(Comment::getCreatedAt))
                .map(commentMapper::toDto).toList();
    }

    public Comment getCommentIfExist(long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment by id: " + commentId + " not found"));
    }


    private void validateAuthorExists(CommentDto commentDto) {
        UserDto userDto = userServiceClient.getUser(commentDto.getAuthorId());
        if (userDto == null || userDto.getId() == null) {
            throw new IllegalArgumentException("There are no author with id " + commentDto.getAuthorId());
        }
    }

    private Comment validateCommentDto(CommentDto commentDto, long id) {
        getPost(id);
        Comment comment = commentRepository.findById(commentDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("There are no comment with id " + commentDto.getId()));

        if (commentDto.getAuthorId() != comment.getAuthorId()) {
            throw new IllegalArgumentException("Only author can make changes! ID: " + commentDto.getAuthorId() + " is not valid");
        }

        if (id != comment.getPost().getId()) {
            throw new IllegalArgumentException("Post's ID is not invalid");
        }

        return comment;
    }
    private Post getPost(long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post ID is invalid {}" + postId));
    }

    private KafkaCommentEvent createKafkaCommentEvent(Comment comment) {
        KafkaCommentEvent kafkaCommentEvent = new KafkaCommentEvent();
        kafkaCommentEvent.setCommentId(comment.getId());
        kafkaCommentEvent.setPostId(comment.getPost().getId());
        kafkaCommentEvent.setAuthorId(comment.getAuthorId());
        return kafkaCommentEvent;
    }

}
