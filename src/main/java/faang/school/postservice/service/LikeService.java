package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.exeption.DataValidationException;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final LikeMapper likeMapper;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserServiceClient userServiceClient;

    public LikeDto addLikeToPost(long postId, LikeDto like) {
        validateLikeOnPost(postId, like);
        Like likeEntity = likeMapper.toEntity(like);
        likeEntity.setPost(postRepository.findById(postId).get());
        return likeMapper.toDto(likeRepository.save(likeEntity));
    }

    public LikeDto addLikeToComment(long commentId, LikeDto like) {
        validateLikeOnComment(commentId, like);
        Like likeEntity = likeMapper.toEntity(like);
        likeEntity.setComment(commentRepository.findById(commentId).get());
        return likeMapper.toDto(likeRepository.save(likeEntity));
    }

    public void deleteLikeFromPost(long postId, long userId) {
        likeRepository.deleteByPostIdAndUserId(postId, userId);
    }

    public void deleteLikeFromComment(long commentId, long userId) {
        likeRepository.deleteByCommentIdAndUserId(commentId, userId);
    }

    public void validateLikeOnPost(long postId, LikeDto likeDto){
        try {
            userServiceClient.getUser(likeDto.getUserId());
        } catch (FeignException e) {
            throw new DataValidationException("User with this Id does not exist !");
        }

        Post post = postRepository.findById(postId).orElseThrow(
                () -> new DataValidationException("Post with this Id does not exist !"));
        List<Like> likes = post.getLikes();
        List<Comment> comments = post.getComments();
        for (Like like: likes){
            if(Objects.equals(like.getUserId(), likeDto.getUserId())){
                throw new DataValidationException("Like on post already exist !");
            }
        }
        comments
                .forEach(comment -> comment.getLikes()
                        .forEach(like -> {
                            if (Objects.equals(like.getUserId(), likeDto.getUserId())){
                                throw new DataValidationException("Cannot like post and comment together !");
                            }
                        }));
    }

    public void validateLikeOnComment(long commentId, LikeDto likeDto){
        try {
            userServiceClient.getUser(likeDto.getUserId());
        } catch (FeignException e) {
            throw new DataValidationException("User with this Id does not exist !");
        }

        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new DataValidationException("Comment with this Id does not exist !"));
        List<Like> likes = comment.getLikes();
        List<Like> likesOnPost = comment.getPost().getLikes();
        for(Like like: likes){
            for(Like likeOnPost: likesOnPost){
                if (Objects.equals(like.getUserId(), likeOnPost.getUserId())){
                    throw new DataValidationException("Cannot like post and comment together !");
                }
            }
            if(Objects.equals(like.getUserId(), likeDto.getUserId())){
                throw new DataValidationException("Like on comment already exist !");
            }
        }
    }
}
