package faang.school.postservice.service.like;

import faang.school.postservice.dto.user.UserDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface LikeService {
    List<UserDto> getUsersLikersByPostId(long postId);

    List<UserDto> getUsersLikerByCommentId(long commentId);
}