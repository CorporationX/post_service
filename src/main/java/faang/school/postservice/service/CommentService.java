package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final UserServiceClient userServiceClient;
    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;

    public CommentDto create(CommentDto commentDto) {
        validateAuthorExists(commentDto);
        Comment comment = commentMapper.CommentDtoToEntity(commentDto);
        return commentMapper.EntityToCommentDto(commentRepository.save(comment));
    }

    public CommentDto change(CommentDto commentDto) {
        validateAuthorExists(commentDto);

    }

    private void validateAuthorExists(CommentDto commentDto) {
        UserDto userDto = userServiceClient.getUser(commentDto.getAuthorId());
        if (userDto == null) {
            throw new IllegalArgumentException("There are no author of comment");
        }

        if (commentDto.getContent().isBlank() || commentDto.getContent().length() > 4096) {
            throw new IllegalArgumentException("Content cannot be empty and longer than 4096 characters");
        }


    }
}
