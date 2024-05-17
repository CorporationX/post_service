package faang.school.postservice.service.like;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Like;
import faang.school.postservice.repository.LikeRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final UserServiceClient userServiceClient;

    static final int BATCH_SIZE = 100;

    public List<UserDto> getAllUsersDtoByPostId (long postId) {
        List<Long> usersIdList = getLikedUserIdsByPost(postId);
        return getAllUsersDto(usersIdList);
    }

    public List<UserDto> getAllUsersDtoByCommentId (long commentId) {
        List<Long> usersIdList = getLikedUserIdsByComment(commentId);
        return getAllUsersDto(usersIdList);
    }


    private List<Long> getLikedUserIdsByPost(long postId) {
        return likeRepository.findByPostId(postId).stream()
                .map(Like::getUserId)
                .toList();
    }

    private List<Long> getLikedUserIdsByComment(long commentId) {
        return likeRepository.findByCommentId(commentId).stream()
                .map(Like::getUserId)
                .toList();
    }

    private List<UserDto> getAllUsersDto(List<Long> userIds) throws FeignException {
        List<CompletableFuture<List<UserDto>>> completableFutures = new ArrayList<>();

        for (int i = 0; i < userIds.size(); i += BATCH_SIZE) {
            int endIndex = Math.min(i + BATCH_SIZE, userIds.size());
            List<Long> batchUserIds = userIds.subList(i, endIndex);

            CompletableFuture<List<UserDto>> future = CompletableFuture.supplyAsync(() -> {
                try {
                    return userServiceClient.getUsersByIds(batchUserIds);
                } catch (FeignException e) {
                    throw new RuntimeException("Not find user", e);
                }
            });

            completableFutures.add(future);
        }

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                completableFutures.toArray(new CompletableFuture[0])
        );

        CompletableFuture<List<UserDto>> allUsersFuture = allFutures.thenApply(v ->
                completableFutures.stream()
                        .map(CompletableFuture::join)
                        .flatMap(List::stream)
                        .collect(Collectors.toList())
        );

        try {
            return allUsersFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Произошла ошибка при выполнении операции", e);
        }
    }
}
