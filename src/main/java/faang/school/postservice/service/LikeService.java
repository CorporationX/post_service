package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Like;
import faang.school.postservice.repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
public class LikeService {

    @Value("${batch-size}")
    @Setter
    private int BATCH_SIZE;

    private final ExecutorService executorService;
    private final LikeRepository likeRepository;
    private final UserServiceClient userServiceClient;

    public List<UserDto> getLikesUsersByPostId(Long postId) {

        List<Long> userIdsLikedPost = likeRepository.findByPostId(postId).stream()
                .map(Like::getUserId)
                .toList();

        return getUsersDto(userIdsLikedPost);
    }

    public List<UserDto> getLikesUsersByCommentId(Long commentId) {

        List<Long> userIdsLikedComment = likeRepository.findByCommentId(commentId).stream()
                .map(Like::getUserId)
                .toList();

        return getUsersDto(userIdsLikedComment);
    }

    private List<UserDto> getUsersDto(List<Long> userIdsLikedComment) {
        List<CompletableFuture<List<UserDto>>> usersDtoFutures = new ArrayList<>();

        for (int i = 0; i < userIdsLikedComment.size(); i += BATCH_SIZE) {
            List<Long> batch = userIdsLikedComment.subList(i, Math.min(i + BATCH_SIZE, userIdsLikedComment.size()));
            usersDtoFutures.add(CompletableFuture.supplyAsync(() -> userServiceClient.getUsersByIds(batch), executorService));
        }
        return getReadyUsersDtoFuture(usersDtoFutures).join();
    }

    private CompletableFuture<List<UserDto>> getReadyUsersDtoFuture(List<CompletableFuture<List<UserDto>>> usersDtoFutures) {
        return CompletableFuture.allOf(usersDtoFutures.toArray(new CompletableFuture[0]))
                .thenApply(v -> usersDtoFutures.stream()
                        .map(CompletableFuture::join)
                        .flatMap(List::stream)
                        .toList());
    }
}
