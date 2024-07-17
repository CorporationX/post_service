package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.LikeServiceValidator;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Supplier;


// перенести связь с репозиториями в отдельный сервис для поста и комментов
// нужно в базу кидать не dto хотя я вроде и не кидаю
// разобраться с 5 пунктом он описан ниже по условию и скрин в тг

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeServiceValidator validator;
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;
    private final CommentRepository commentRepository;
    private final LikeMapper likeMapper;

    public void addLikeToPost(long userId, long postId) {
        Post post = getPost(postId);
        verifyUser(userId);
        Like like = getLike(() -> likeRepository.findByPostIdAndUserId(postId, userId));

        post.getLikes().add(like);
        likeRepository.save(like);
    }

    public void deleteLikeFromPost(LikeDto likeDto) {
        long postId = likeDto.getPostId();
        Post post = getPost(postId);

        post.getLikes().remove(likeDto.getId());
        likeRepository.deleteByPostIdAndUserId(postId, likeDto.getUserId());
    }

    public void addLikeTOComment(long userId, long commentId) {
        Comment comment = getComment(commentId);
        verifyUser(userId);
        Like like = getLike(() -> likeRepository.findByCommentIdAndUserId(commentId, userId));

        comment.getLikes().add(like);
        likeRepository.save(like);
    }

    public void deleteLikeFromComment(LikeDto likeDto) {
        long commentId = likeDto.getCommentId();
        Comment comment = getComment(commentId);

        comment.getLikes().remove(likeDto.getId());
        likeRepository.deleteByCommentIdAndUserId(commentId, likeDto.getUserId());
    }

    public long getCountLikeForPost(long postId) {
        return getPost(postId).getLikes().size();
    }

    private Post getPost(long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post with the same id does not exist"));
    }

    private Comment getComment(long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment with the same id does not exist"));
    }

    private Like getLike(Supplier<Optional<Like>> likeSupplier) {
        Optional<Like> likeOptional = likeSupplier.get();
        validator.validActiveLike(likeOptional);
        return likeOptional.get();
    }

    private void verifyUser(long userId) {
        try {
            UserDto userDto = userServiceClient.getUser(userId);
        } catch (FeignException e) {
            throw new IllegalArgumentException("User with id not found");
        }
    }
}

