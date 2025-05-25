package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.CommentDtoStatus;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    private CommentRepository commentRepository;
    private CommentMapper commentMapper;
    private PostService postService;
    private UserServiceClient userServiceClient;

    @Override
    public CommentDto create(CommentDto commentDto) {
        Post post = postService.findById(commentDto.getPostId());
        userServiceClient.getUser(commentDto.getAuthorId());

        commentDto.setCreatedAt(LocalDateTime.now());
        commentDto.setStatus(CommentDtoStatus.CREATED);
        Comment comment = commentMapper.toEntity(commentDto);
        comment.setPost(post);
        return commentMapper.toDto(commentRepository.save(comment));
    }

    @Override
    public CommentDto update(CommentDto commentDto) {
        Comment comment = findCommentById(commentDto.getId());
        comment = commentMapper.updateEntityFromDto(commentDto, comment);
        comment.setUpdatedAt(LocalDateTime.now());
        commentDto.setStatus(CommentDtoStatus.UPDATED);
        return commentMapper.toDto(commentRepository.save(comment));
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
