package faang.school.postservice.service.like;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.LikedException;
import faang.school.postservice.model.Like;
import faang.school.postservice.repository.CommentRepository;
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
    @Override
    public Like likeThePost(Like like, long postId) {
        UserDto user = userServiceClient.getUser(userContext.getUserId());
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
         UserDto userDto = userServiceClient.getUser(userContext.getUserId());
         Optional<Like> likeOptional = likeRepository.findByPostIdAndUserId(postId, userDto.id());
         Like like = likeOptional.get();
         likeRepository.deleteById(like.getId());
    }

    @Override
    public Like likeTheComment(Like like, long commentId) {
        UserDto user = userServiceClient.getUser(userContext.getUserId());
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
        UserDto user = userServiceClient.getUser(userContext.getUserId());
        Optional<Like> likeOptional = likeRepository.findByCommentIdAndUserId(commentId, user.id());
        Like like = likeOptional.get();
        likeRepository.deleteById(like.getId());
    }
}
