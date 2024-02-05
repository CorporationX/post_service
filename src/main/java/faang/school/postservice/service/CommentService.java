package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.CommentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final UserServiceClient userServiceClient;
    public CommentDto create(CommentDto commentDto) {
        validateAuthorExists(commentDto);

    }

    public void validateAuthorExists(CommentDto commentDto) {
        UserDto userDto = userServiceClient.getUser(commentDto.getAuthorId());
        if (userDto == null) {
            throw new IllegalArgumentException("There are no author of comment");
        }
    }
}
