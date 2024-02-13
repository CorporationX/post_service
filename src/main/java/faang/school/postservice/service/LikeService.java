package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Like;
import faang.school.postservice.repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final UserServiceClient userServiceClient;

    private List<List<Long>> createBatchesList (List<Long> listUsers) {
        int batchSize = 100;

        List<List<Long>> batches = new ArrayList<>();

        for (int i = 0; i < listUsers.size(); i += batchSize) {
            int endIndex = Math.min(i + batchSize, listUsers.size());
            List<Long> batch = listUsers.subList(i, endIndex);
            batches.add(batch);
        }

        return batches;
    }

    private List<UserDto> getUsersFromBatches(List<List<Long>> batches) {
        CompletableFuture<Void> future = CompletableFuture.completedFuture(null);
        List<UserDto> combinedData = new ArrayList<>();

        for (List<Long> batchGroup : batches) {
            CompletableFuture<List<UserDto>> requestFuture = CompletableFuture.supplyAsync(() -> {
                return userServiceClient.getUsersByIds(batchGroup);
            });

            future = future.thenCompose(ignored -> requestFuture).thenAccept(responseData -> {
                combinedData.addAll(responseData);
            });
        }

        future.join();

        return combinedData;
    }


    public List<UserDto> getLikeAtPost(long postId) {
        List<Long> usersLike = likeRepository.findByPostId(postId).stream().map(Like::getUserId).toList();

        if (usersLike.size() <= 100) {
            return userServiceClient.getUsersByIds(usersLike);
        } else {
            List<List<Long>> batches = createBatchesList(usersLike);
            CompletableFuture<List<UserDto>> future = CompletableFuture.supplyAsync(() -> getUsersFromBatches(batches));
            return future.join();
        }
    }

    public List<UserDto> getLikeAtComment (Long commentId) {
        List<Long> usersLike = likeRepository.findByCommentId(commentId).stream().map(Like::getUserId).toList();

        if (usersLike.size() <= 100) {
            return userServiceClient.getUsersByIds(usersLike);
        } else {
            List<List<Long>> batches = createBatchesList(usersLike);
            CompletableFuture<List<UserDto>> future = CompletableFuture.supplyAsync(() -> getUsersFromBatches(batches));
            return future.join();
        }
    }
}
