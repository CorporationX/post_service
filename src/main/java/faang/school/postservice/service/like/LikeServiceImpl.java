package faang.school.postservice.service.like;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.externalservice.ExternalServiceConnectException;
import faang.school.postservice.model.Like;
import faang.school.postservice.repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import static faang.school.postservice.utils.Utils.chunked;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {
    private final LikeRepository likeRepository;
    private final UserServiceClient userServiceClient;
    private final Integer chunkSize;

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getUsersLikersByPostId(long postId) {
        log.info("Fetching post likers: postId={}, chunkSize={}", postId, chunkSize);
        List<Like> likes = Optional.ofNullable(likeRepository.findByPostId(postId))
                .orElseGet(List::of);
        log.debug("Raw likes fetched for postId {} -> {} likes", postId, likes.size());

        return handleExceptions(() -> requestToServiceClient(likes));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getUsersLikerByCommentId(long commentId) {
        log.info("Fetching comment likers: commentId={}, chunkSize={}", commentId, chunkSize);
        List<Like> likes = Optional.ofNullable(likeRepository.findByCommentId(commentId))
                .orElseGet(List::of);
        log.debug("Raw likes fetched for commentId {} -> {} likes", commentId, likes.size());
        return handleExceptions(() -> requestToServiceClient(likes));
    }

    private List<UserDto> requestToServiceClient(List<Like> likes) {
        List<Long> userIds = likes.stream()
                .filter(Objects::nonNull)
                .map(Like::getUserId)
                .distinct()
                .toList();

        if (userIds.isEmpty()) {
            log.info("No userIds resolved from likes -> returning empty list");
            return List.of();
        }
        List<List<Long>> chunks = chunked(userIds, chunkSize);
        log.info("Resolved {} unique userIds -> {} chunks (size={})",
                userIds.size(), chunks.size(), chunkSize);

        return chunks.stream()
                .filter(Objects::nonNull)
                .map(userServiceClient::getUsersByIds)
                .flatMap(List::stream)
                .toList();
    }

    private <T> T handleExceptions(Supplier<T> fn) {
        try {
            return fn.get();
        } catch (RuntimeException e) {
            throw new ExternalServiceConnectException("userService", "The service is not available", e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}