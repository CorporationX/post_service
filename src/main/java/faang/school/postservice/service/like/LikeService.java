package faang.school.postservice.service.like;

import com.google.common.collect.Lists;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.like.LikeOperatingException;
import faang.school.postservice.exception.like.LikeOperatingExceptionMessage;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import java.util.List;

import static faang.school.postservice.exception.like.LikeOperatingExceptionMessage.NON_EXISTING_COMMENT_EXCEPTION;
import static faang.school.postservice.exception.like.LikeOperatingExceptionMessage.NON_EXISTING_POST_EXCEPTION;

@Service
@RequiredArgsConstructor
public class LikeService {
    public static final int BATCH_SIZE = 100;
    private final UserServiceClient userServiceClient;
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public List<UserDto> getUsersLikedPostByPostId(long postId) {
        Post post = getLikedObject(postRepository, postId, NON_EXISTING_POST_EXCEPTION);

        return getUsersLikedObject(post.getLikes());
    }

    public List<UserDto> getUsersLikedCommentByCommentId(long commentId) {
        Comment comment = getLikedObject(commentRepository, commentId, NON_EXISTING_COMMENT_EXCEPTION);

        return getUsersLikedObject(comment.getLikes());
    }

    private List<UserDto> getUsersLikedObject(List<Like> likes) {
        List<Long> usersLikedPost = likes.stream()
                .map(Like::getUserId)
                .toList();

        List<List<Long>> usersLikedPostBatched = Lists.partition(usersLikedPost, BATCH_SIZE);

        return usersLikedPostBatched.parallelStream()
                .map(userServiceClient::getUsersByIds)
                .flatMap(List::stream)
                .toList();
    }

    private <T> T getLikedObject(CrudRepository<T, Long> repository,
                                 long likedObjectId,
                                 LikeOperatingExceptionMessage exceptionMessage) {
        return repository.findById(likedObjectId)
                .orElseThrow(() -> new LikeOperatingException(exceptionMessage.getMessage()));
    }
}
