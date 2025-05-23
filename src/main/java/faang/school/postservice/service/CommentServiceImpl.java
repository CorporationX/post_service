package faang.school.postservice.service;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.CommentDtoStatus;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    private CommentRepository commentRepository;
    private CommentMapper commentMapper;
    private PostRepository postRepository;

    public CommentDto create(long creatorId, CommentDto commentDto) {
        commentDto.setAuthorId(creatorId);
        commentDto.setCreatedAt(LocalDateTime.now());
        commentDto.setStatus(CommentDtoStatus.CREATED);
        Comment comment = commentMapper.toEntity(commentDto);
        Long postId = commentDto.getPostId();
        Post post = postRepository.findById(postId).orElseThrow(() ->
                new EntityNotFoundException
                (String.format("There is no post with id %d", postId)));
        comment.setPost(post);
        return commentMapper.toDto(commentRepository.save(comment));
    }

    public CommentDto update(CommentDto commentDto) {
        Long id = commentDto.getId();
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException
                        (String.format("There is no comment with id %d", id)));
        comment = commentMapper.updateEntityFromDto(commentDto, comment);
        comment.setUpdatedAt(LocalDateTime.now());
        commentDto.setStatus(CommentDtoStatus.UPDATED);
        return commentMapper.toDto(commentRepository.save(comment));
    }

    public CommentDto findById(long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException
                        (String.format("There is no comment with id %d", commentId)));
        return commentMapper.toDto(comment);
    }


    public void deleteById(long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException
                        (String.format("There is no comment with id %d", commentId)));
        commentRepository.delete(comment);
    }

    @Override
    public List<CommentDto> findByPostId(long postId) {
        return commentMapper.toListDto(commentRepository.findAllByPostId(postId).stream()
                .sorted(Comparator.comparing(Comment::getCreatedAt))
                .toList());
    }
}
