package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
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
    private final CommentRepository repository;
    private final CommentMapper mapper;
    private final PostRepository postRepository;
    private final UserServiceClient client;
    private static final int MAX_LENGTH = 4096;

    public CommentDto createComment(long userId, long postId, CommentDto commentDto) {
        UserDto user = client.getUser(userId);
        if (user == null) {
            throw new DataValidationException("пользователь на найден");
        }
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new DataValidationException("Пост с id %d не найден", postId));
        validateNullCommentDto(commentDto);
        validateCommentContent(commentDto);
        Comment commentForSave = mapper.toEntity(commentDto);
        Comment savedComment = repository.save(commentForSave);
        log.info("Комментарий {} успешно опубликован", savedComment.getId());
        return mapper.toDto(savedComment);
    }

    public CommentDto editComment(CommentDto commentDto, long commentId, String content) {
        Comment targetComment = repository.findById(commentId)
                .orElseThrow(() -> new DataValidationException("Комментарий %d не найден", commentId));

        validateNullCommentDto(commentDto);
        validateCommentContent(commentDto);
        targetComment.setContent(content);

        repository.save(targetComment);
        log.info("Комментарий {} успешно отредактирован", commentId);

        return mapper.toDto(targetComment);
    }

    public List<CommentDto> getAllComments(long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new DataValidationException("Пост не найден"));

        return repository.findAllByPostId(postId).stream()
                .sorted(Comparator.comparing(Comment::getCreatedAt))
                .map(mapper::toDto)
                .toList();
    }

    public void deleteComment(long commentId) {
        repository.findById(commentId)
                .orElseThrow(() -> new DataValidationException("Комментарий %d не найден", commentId));
        log.info("Комментарий {} успешно удален", commentId);
        repository.deleteById(commentId);
    }

    private void validateCommentContent(CommentDto commentDto) {
        if (commentDto.content() == null || commentDto.content().isBlank()) {
            throw new DataValidationException("комментарий не может быть пустым");
        }

        int contentLength = commentDto.content().length();

        if (contentLength > MAX_LENGTH) {
            throw new DataValidationException(
                    "Максимальная длина комментария %d символов, вы ввели %d", MAX_LENGTH, contentLength);
        }
        System.out.println("maxLength: " + MAX_LENGTH);
    }

    private void validateNullCommentDto(CommentDto commentDto) {
        if (commentDto == null) {
            throw new DataValidationException("Комментарий не может быть null");
        }
    }

}
