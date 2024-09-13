package faang.school.postservice.service.impl.like;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Like;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.service.LikeService;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Slf4j
@Service
@Getter
@RequiredArgsConstructor
public class LikeParallelServiceImpl implements LikeService {

    private static final int BATCH_SIZE = 1000;

    private final LikeRepository likeRepository;
    private final UserServiceClient userServiceClient;
    private final ExecutorService executor = Executors.newCachedThreadPool();

    @Override
    public List<UserDto> getUsersLikedPost(long postId) {
        List<Like> likes = likeRepository.findByPostId(postId);
        return getUserDtos(likes);
    }

    @Override
    public List<UserDto> getUsersLikedComment(long commentId) {
        List<Like> likes = likeRepository.findByCommentId(commentId);
        return getUserDtos(likes);
    }

    private List<UserDto> getUserDtos(List<Like> likes) {
        List<Long> userIds = likesToUserIds(likes);
        var batches = divideIntoBatches(userIds);
        var futures = fetchUsersInParallel(batches);
        return futuresToUsers(futures);
    }

    private List<Long> likesToUserIds(List<Like> likes) {
        return likes.parallelStream()
                .map(Like::getUserId)
                .toList();
    }

    private Stream<List<Long>> divideIntoBatches(List<Long> userIds) {
        return IntStream.iterate(0,
                        (i) -> i < userIds.size(),
                        (i) -> i + 100)
                .mapToObj(i -> {
                    int lastIndex = Math.min(i + BATCH_SIZE, userIds.size());
                    return userIds.subList(i, lastIndex);
                });
    }

    private List<CompletableFuture<List<UserDto>>> fetchUsersInParallel(Stream<List<Long>> batches) {
        return batches.map(batch -> CompletableFuture.supplyAsync(
                                () -> userServiceClient.getUsersByIds(batch), executor)
                        .exceptionally(this::handleException))
                .toList();
    }

    private List<UserDto> handleException(Throwable exception) {
        String message = "Fetching users from parallel batch failed.\nMessage:\n " + exception.getMessage();
        log.warn(message);
        return List.of();
    }

    private static List<UserDto> futuresToUsers(List<CompletableFuture<List<UserDto>>> futures) {
        return futures.stream()
                .flatMap(future -> future.join().stream())
                .toList();
    }

    @PreDestroy
    public void shutdownExecutor() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException exception) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
