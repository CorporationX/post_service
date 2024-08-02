package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CreateCommentDto;
import faang.school.postservice.dto.comment.UpdatedCommentDto;
import faang.school.postservice.exception.NotFoundException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;
    private final CommentMapper commentMapper;

    public CommentDto createComment(CreateCommentDto createCommentDto) {
        checkUserExistence(createCommentDto);
        Comment savedComment = commentMapper.toEntity(createCommentDto);
        savedComment = commentRepository.save(savedComment);
        log.info("Comment with ID = {} was created", savedComment.getId());
        return commentMapper.toDto(savedComment);
    }

    private void checkUserExistence(CreateCommentDto createCommentDto) {
        Long authorId = createCommentDto.getAuthorId();
        userServiceClient.getUser(authorId);
    }

    public CommentDto updateComment(UpdatedCommentDto updatedCommentDto) {
        Comment comment = commentRepository.findById(updatedCommentDto.getId())
                .orElseThrow(() -> new NotFoundException("Comment with ID = " + updatedCommentDto.getId() + " does not found"));
        comment.setContent(updatedCommentDto.getContent());
        Comment updatedComment = commentRepository.save(comment);
        log.info("Comment with ID = {} was updated", updatedCommentDto.getId());
        return commentMapper.toDto(updatedComment);
    }

    public List<CommentDto> getAllCommentsByPostIdSortedByCreatedDate(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new NotFoundException("Post with ID = " + postId + "does not exist");
        }
        List<Comment> postsComments = commentRepository
                .findAllByPostId(postId)
                .stream()
                .sorted(Comparator.comparing(Comment::getCreatedAt))
                .toList();
        log.info("{} comments for post with ID = {} was found and sorted by created date", postsComments.size(), postId);
        return commentMapper.toDtoList(postsComments);
    }

    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
        log.info("Comment with ID = {} was deleted", id);
    }
}
