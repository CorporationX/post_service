package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.NotFoundException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.util.validator.comment.CommentServiceValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentServiceValidator validator;
    private final PostService postService;
    private final CommentMapper commentMapper;

    @Transactional
    public CommentDto createComment(CommentDto commentDto) {
        validator.validateExistingUserAtCommentDto(commentDto);

        Comment comment = commentMapper.toEntity(commentDto);

        return commentMapper.toDto(commentRepository.save(comment));
    }
}
