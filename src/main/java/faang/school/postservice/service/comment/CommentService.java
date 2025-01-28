package faang.school.postservice.service;

import faang.school.postservice.dto.comment.CommentReadDto;
import faang.school.postservice.dto.comment.CommentCreateDto;
import faang.school.postservice.dto.comment.CommentUpdateDto;
import faang.school.postservice.exception.BusinessException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final PostService postService;

    public CommentReadDto create(CommentCreateDto createDto) {
        verifyCommentCreation(createDto);

        Comment newComment = commentMapper.toEntity(createDto);
        newComment = commentRepository.save(newComment);
        return commentMapper.toDto(newComment);
    }

    public CommentReadDto update(CommentUpdateDto updateDto) {
        Comment comment = getCommentById(updateDto.id());

        validateEditorAndAuthorEquality(updateDto.editorId(), comment.getAuthorId());

        commentMapper.updateEntityFromDto(updateDto, comment);
        commentRepository.save(comment);

        return commentMapper.toDto(comment);
    }

    public List<CommentReadDto> getCommentsByPostId(long postId) {
        List<Comment> comments = commentRepository.findAllByPostId(postId);

        return comments.stream()
                .map(commentMapper::toDto)
                .toList();
    }

    public void remove(long commentId) {
        commentRepository.deleteById(commentId);
    }

    private Comment getCommentById(long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Комментария с ID " + commentId + " не найден"));
    }

    private void validateEditorAndAuthorEquality (long editorId, long authorId) {
        if (editorId != authorId) {
            throw new BusinessException("Редактировать комментарий может только его автор");
        }
    }

    private void verifyCommentCreation(CommentCreateDto createDto) {
        userService.getUserDtoById(createDto.authorId());
        postService.getPostById(createDto.postId());
    }

}
