package faang.school.postservice.service;

import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.validator.CommentValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final CommentValidator commentValidator;
    private final PostService postService;


    @Transactional
    public CommentDto createComment(Long postId, CommentDto commentDto) {
        commentValidator.validateUserBeforeCreate(commentDto);
        Post post = postService.getPostById(postId);
        Comment comment = commentMapper.toEntity(commentDto);
        comment.setPost(post);
        return commentMapper.toDto(commentRepository.save(comment));
    }
}
