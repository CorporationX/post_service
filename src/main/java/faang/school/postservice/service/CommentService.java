package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final UserServiceClient userServiceClient;
    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public CommentDto create(CommentDto commentDto) {
        validateAuthorExists(commentDto);
        Comment comment = commentMapper.commentDtoToEntity(commentDto);
        return commentMapper.entityToCommentDto(commentRepository.save(comment));
    }

    public CommentDto update(CommentDto commentDto, long id) {
        Comment comment = validateToUpdate(commentDto, id);

        if (comment.getContent().equals(commentDto.getContent())) {
            throw new IllegalArgumentException("There are no changes made");
        }

        comment.setContent(commentDto.getContent());
        return commentMapper.entityToCommentDto(commentRepository.save(comment));
    }

    public void delete(CommentDto commentDto, long id) {
        Comment comment = validateToUpdate(commentDto, id);
        commentRepository.delete(comment);
    }

    public List<CommentDto> getAllCommentsByPostId(long id) {
        List<Comment> commentList = commentRepository.findAllByPostId(id);

        if (commentList == null) {
            throw new IllegalArgumentException("There are no comments or post's id is invalid");
        }

        return commentList.stream()
                .sorted(Comparator.comparing(Comment::getCreatedAt))
                .map(commentMapper::entityToCommentDto).toList();
    }

    private void validateAuthorExists(CommentDto commentDto) {
        UserDto userDto = userServiceClient.getUser(commentDto.getAuthorId());
        if (userDto == null || userDto.getId() == null) {
            throw new IllegalArgumentException("There are no author with id "+commentDto.getAuthorId());
        }

        if (commentDto.getContent().isBlank() || commentDto.getContent().length() > 4096) {
            throw new IllegalArgumentException("Content cannot be empty and longer than 4096 characters");
        }
    }

    private Comment validateToUpdate(CommentDto commentDto, long id) {
        postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("There are no post with id "+id));
        Comment comment = commentRepository.findById(commentDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("There are no comment with id "+commentDto.getId()));

        if (commentDto.getAuthorId() != comment.getAuthorId()) {
            throw new IllegalArgumentException("Only author can make changes! ID: "+commentDto.getAuthorId()+" is not valid");
        }

        if (id != comment.getPost().getId()) {
            throw new IllegalArgumentException("Post's ID is not invalid");
        }

        return comment;
    }
}
