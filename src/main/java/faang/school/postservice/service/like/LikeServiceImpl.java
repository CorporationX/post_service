package faang.school.postservice.service.like;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final LikeRepository likeRepository;
    private final UserServiceClient userServiceClient;
    private final UserContext userContext;
    private final PostService postService;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public Like likeThePost(Like like, long postId) {
        UserDto user = existenceCheckUser();
        Post post = postService.getPostById(postId);
        Optional<Like> likeOptional = likeRepository.findByPostIdAndUserId(post.getId(), user.id());
        Like existingLike = likeOptional.orElseGet(() -> {
            Like newLike = new Like();
            newLike.setPost(post);
            newLike.setUserId(user.id());
            return newLike;
        });
        return likeRepository.save(existingLike);
    }

    @Override
    public void deleteLikeThePost(long postId) {
        UserDto user = existenceCheckUser();
        likeRepository.deleteByPostIdAndUserId(postId, user.id());
    }

    @Override
    @Transactional
    public Like likeTheComment(Like like, long commentId) {
        UserDto user = existenceCheckUser();
        Optional<Comment> comment = commentRepository.findById(commentId);
        Optional<Like> likeOptional = likeRepository.findByCommentIdAndUserId(commentId, user.id());
        Like likeResult = likeOptional.orElseGet(() -> {
            Like newLike = new Like();
            newLike.setComment(comment.get());
            newLike.setUserId(user.id());
            return newLike;
        });
        return likeRepository.save(likeResult);
    }

    @Override
    public void deleteLikeTheComment(long commentId) {
        UserDto user = existenceCheckUser();

        likeRepository.deleteByCommentIdAndUserId(commentId, user.id());
    }

    @Override
    @Transactional
    public long countTheLikeForPost(long postId) {
        Post posts = postService.getPostById(postId);

        return posts.getLikes().size();
    }

    protected UserDto existenceCheckUser() {
        return userServiceClient.getUser(userContext.getUserId());
    }
}
