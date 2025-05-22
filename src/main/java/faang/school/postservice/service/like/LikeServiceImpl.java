package faang.school.postservice.service.like;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.LikeThePostCannotException;
import faang.school.postservice.exception.LikedException;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validation.like.LikeValidation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService{

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserServiceClient userServiceClient;
    private final UserContext userContext;
    @Override
    public Like likeThePost(Like like, long postId) {
        UserDto user = userServiceClient.getUser(userContext.getUserId());
        LikeValidation.validationPost(postId);
        Optional<Like> likeOptional = likeRepository.findByPostIdAndUserId(postId, user.id());
        Like optionalLike = likeOptional.get();
        if(optionalLike.getPost().getId() == postId){
            throw new LikedException("The Post already has a Like");
        } else {
            return likeRepository.save(like);
        }
    }
}
