package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Like;
import faang.school.postservice.repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {
    @Value("${user-service.maxNumberUsersInRequest}")
    private int maxNumberUsersInRequest;
    private final LikeRepository likeRepository;
    private final UserServiceClient userServiceClient;

    @Override
    public List<UserDto> findAllUserWhoLikedPost(Long postId) {
        List<Like> likesToPost = likeRepository.findByPostId(postId);
        List<Long> userIds = likesToPost.stream().map(Like::getUserId).toList();
        List<List<UserDto>> chunksUserDto = splitUsers(userIds);
        return chunksUserDto.stream().flatMap(List::stream).collect(Collectors.toList());
    }

    @Override
    public List<UserDto> findAllUserWhoLikedComment(Long commentId) {
        List<Like> likesToPost = likeRepository.findByCommentId(commentId);
        List<Long> userIds = likesToPost.stream().map(Like::getUserId).toList();
        List<List<UserDto>> chunksUserDto = splitUsers(userIds);
        return chunksUserDto.stream().flatMap(List::stream).collect(Collectors.toList());
    }

    private List<List<UserDto>> splitUsers(List<Long> ids) {
        List<List<UserDto>> chunksUserDto = new ArrayList<>();
        for (int i = 0; i < ids.size(); i += maxNumberUsersInRequest) {
            List<Long> chunkIds = ids.subList(i, Math.min(ids.size(), i + maxNumberUsersInRequest));
            chunksUserDto.add(userServiceClient.getUsersByIds(chunkIds));
        }
        return chunksUserDto;
    }
}
