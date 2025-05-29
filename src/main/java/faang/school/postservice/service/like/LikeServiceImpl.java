package faang.school.postservice.service.like;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.LikedException;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validation.comment.CommentValidation;
import faang.school.postservice.validation.post.PostValidation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService{

    private final LikeRepository likeRepository;
    private final UserServiceClient userServiceClient;
    private final UserContext userContext;
    private final PostRepository postRepository;
    @Override
    public Like likeThePost(Like like, long postId) {
        UserDto user = existenceCheckUser();
        PostValidation.existenceCheckThePost(postId);
        Optional<Like> likeOptional = likeRepository.findByPostIdAndUserId(postId, user.id());
        Like optionalLike = likeOptional.get();
        if(optionalLike.getPost().getId() == postId){
            throw new LikedException("The Post already has a Like");
        } else {
            return likeRepository.save(like);
        }
    }

    @Override
    public void deleteLikeThePost(long postId) {
        UserDto user = existenceCheckUser();

         likeRepository.deleteByPostIdAndUserId(postId, user.id());
    }

    @Override
    public Like likeTheComment(Like like, long commentId) {
        UserDto user = existenceCheckUser();
        CommentValidation.existenceCheckTheComment(commentId);
        Optional<Like> likeOptional = likeRepository.findByCommentIdAndUserId(commentId, user.id());
        Like likeResult = likeOptional.get();
        if(likeResult.getComment().getId() == commentId) {
            throw new LikedException("The Comment already has a Like") ;
        } else {
            return likeRepository.save(like);
        }
    }

    @Override
    public void deleteLikeTheComment(long commentId) {
        UserDto user = existenceCheckUser();

        likeRepository.deleteByCommentIdAndUserId(commentId, user.id());
    }

    @Override
    public long countTheLikeForPost(long postId) {
        Optional<Post> posts = postRepository.findById(postId);
        int countLike = 0;
        if(posts.isPresent()) {
            Post post = posts.get();
            countLike = post.getLikes().size();
        }
        return countLike;
    }

    private UserDto existenceCheckUser(){
        return userServiceClient.getUser(userContext.getUserId());
    }
}
