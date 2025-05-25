package faang.school.postservice.service.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
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

    @Override
    public CommentDto create(CommentDto commentDto) {
        Post post = postService.findPostById(commentDto.getPostId());
        userServiceClient.getUser(commentDto.getAuthorId());

        commentDto.setCreatedAt(LocalDateTime.now());
        commentDto.setStatus(CommentDtoStatus.CREATED);
        Comment comment = commentMapper.toEntity(commentDto);
        comment.setPost(post);
        return commentMapper.toDto(commentRepository.save(comment));
    }

    @Override
    public CommentDto update(CommentDto commentDto) {
        Comment existingComment = findCommentById(commentDto.getId());
        Comment comment = commentMapper.updateEntityFromDto(commentDto, existingComment);
        comment.setUpdatedAt(LocalDateTime.now());
        commentDto.setStatus(CommentDtoStatus.UPDATED);
        comment = commentRepository.save(comment);
        return commentMapper.toDto(comment);
    }

    @Override
    public CommentDto findById (long commentId){
        return commentMapper.toDto(findCommentById(commentId));
    }

    @Override
    public void deleteById(long commentId) {
        Comment comment = findCommentById(commentId);
        commentRepository.delete(comment);
    }

    @Override
    public List<CommentDto> findByPostId(long postId) {
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
