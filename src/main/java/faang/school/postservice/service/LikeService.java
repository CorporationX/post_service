package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Like;
import faang.school.postservice.repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeService {
    final LikeRepository likeRepository;
    final UserServiceClient userServiceClient;

    public final String USER_NOT_EXIST = "User not exist";
    public final String POST_NOT_EXIST = "Post not exist";


    public void addToPost(Like entity) {

        UserDto userDto = userServiceClient.getUser(entity.getUserId());
        if (userDto == null) {
            log.error("Id = {}. {}", entity.getUserId(), USER_NOT_EXIST);
            throw new RuntimeException(USER_NOT_EXIST);
        }

        if (!likeRepository.existsById(entity.getPost().getId())) {
            log.error("Id = {}. {}", entity.getPost().getId(), POST_NOT_EXIST);
            throw new RuntimeException(POST_NOT_EXIST);
        }


        if (Objects.equals(entity.getPost().getId(), 0) {
            log.error(POST_NOT_EXIST);

        }


    }
}
