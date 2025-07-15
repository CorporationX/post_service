package faang.school.postservice.service.like;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.service.comment.CommentService;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final LikeRepository likeRepository;
    private final UserServiceClient userServiceClient;
    private final UserContext userContext;
    private final PostService postService;
    private final CommentService commentService;

    @Override
    @Transactional
    public Like likeThePost(long postId) {
        UserDto user = existenceCheckUser();
        Post post = postService.getPostById(postId);
        Optional<Like> likeOptional = likeRepository.findByPostIdAndUserId(post.getId(), user.getId());
        if (likeOptional.isPresent()) {
            return likeOptional.get();
        } else {
            Like existingLike = likeOptional.orElseGet(() -> {
                Like newLike = new Like();
                newLike.setPost(post);
                newLike.setUserId(user.getId());
                return newLike;
            });
            return likeRepository.save(existingLike);
        }
    }

    @Override
    @Transactional
    public void deleteLikeThePost(long postId) {
        UserDto user = existenceCheckUser();
        likeRepository.deleteByPostIdAndUserId(postId, user.getId());
    }

    @Override
    @Transactional
    public Like likeTheComment(long commentId) {
        UserDto user = existenceCheckUser();
        Comment comment = commentService.getComment(commentId);
        Optional<Like> likeOptional = likeRepository.findByCommentIdAndUserId(commentId, user.getId());
        if (likeOptional.isPresent()) {
            return likeOptional.get();
        } else {
            Like likeResult = likeOptional.orElseGet(() -> {
                Like newLike = new Like();
                newLike.setComment(comment);
                newLike.setUserId(user.getId());
                return newLike;
            });
            return likeRepository.save(likeResult);
        }
    }

    @Override
    @Transactional
    public void deleteLikeTheComment(long commentId) {
        UserDto user = existenceCheckUser();

        likeRepository.deleteByCommentIdAndUserId(commentId, user.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Like> getAllTheLikeForPost(long postId) {
        Post posts = postService.getPostById(postId);
        return posts.getLikes();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Like> getAllTheLikeForComment(long commentId) {
        Comment comment = commentService.getComment(commentId);
        return comment.getLikes();
    }

    protected UserDto existenceCheckUser() {
        return userServiceClient.getUser(userContext.getUserId());
    }
}
