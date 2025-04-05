package faang.school.postservice.service.like;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.user.UserDto;

import java.util.List;

public interface LikeService {
    List<UserDto> getUsersWhoLikedPost(Long postId);

    List<UserDto> getUsersWhoLikedComment(Long commentId);
    void createLikeForPost(Long postId);

    void createLikeForComment(Long commentId);

    void deleteLikeFromPost(long postId);

    void deleteLikeFromComment(long commentId);

    List<LikeDto> getLikes(long postId);
}