package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CreateCommentDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CheckUserService {

    private final UserServiceClient userServiceClient;

    public void checkUserExistence(CreateCommentDto createCommentDto) {
        Long authorId = createCommentDto.getAuthorId();
        userServiceClient.getUser(authorId);
    }
}
