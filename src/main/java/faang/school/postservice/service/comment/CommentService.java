package faang.school.postservice.service.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserServiceClient userServiceClient;
    private final PostService postService;
    private final CommentMapper commentMapper;

    public CommentDto addCommentService(Long id, CommentDto commentDto) {
        validateAuthorExists(commentDto.getAuthorId());
        Comment comment = commentMapper.toEntity(commentDto);
        comment.setPost(postService.getPost(id));
        comment.setLikes(new ArrayList<>());
        commentRepository.save(comment);
        return commentMapper.toDto(comment);
    }

    public CommentDto updateCommentService(Long id, CommentDto commentDto) {
        validateAuthorExists(commentDto.getAuthorId());
        Comment comment = commentRepository.findAllByPostId(id).stream()
                .filter(commentOne -> commentOne.getId() == commentDto.getId())
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Комментария с таким id нет!"));
        comment.setContent(commentDto.getContent());
        commentRepository.save(comment);
        return commentMapper.toDto(comment);
    }

    public List<CommentDto> getCommentsService(Long id) {
        return postService.getPost(id).getComments().stream()
                .map(commentMapper::toDto)
                .toList();
    }

    public CommentDto deleteCommentService(Long id, CommentDto commentDto) {
        validateAuthorExists(commentDto.getAuthorId());
        Comment comments = findCommentInPost(id, commentDto);
        commentRepository.deleteById(comments.getId());
        return commentMapper.toDto(comments);
    }

    private Comment findCommentInPost(Long id, CommentDto commentDto) {
        return commentRepository.findAllByPostId(id).stream()
                .filter(comment ->
                        comment.getAuthorId() == commentDto.getAuthorId() &&
                                comment.getContent().equals(commentDto.getContent()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Комментарий с таким содержанием и автором не найден"));
    }

    private void validateAuthorExists(long authorId) {
        UserDto user = userServiceClient.getUser(authorId);
        if (user == null) {
            log.error("Пользователь с таким ID не найден");
            throw new NullPointerException("Пользователь с таким ID не найден");
        }
    }
}
