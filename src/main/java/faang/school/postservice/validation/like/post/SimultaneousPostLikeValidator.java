package faang.school.postservice.validation.like.post;

import org.springframework.stereotype.Component;

import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.exception.NotFoundException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validation.like.SimultaneousLikeValidator;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
class SimultaneousPostLikeValidator implements SimultaneousLikeValidator {
    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    
    @Override
    public void verifyNotExists(LikeDto dto) {
        verifyUserDidntAlreadyLikedPost(dto);
        
        verifyLikeNotOnPostAndCommentSimultaneously(dto);
    }
    
    private void verifyLikeNotOnPostAndCommentSimultaneously(LikeDto dto) {
        if (likeOnPostAndCommentExist(dto)) {
            throwAlreadyExistsException(String.format(
                "User with id %d can't like post and comment simultaneously",
                dto.getUserId()
            ));
        }
    }
    
    private boolean likeOnPostAndCommentExist(LikeDto dto) {
        Long postId = dto.getPostId();
        Long userId = dto.getUserId();
        
        return getPostById(postId).getComments()
            .stream()
            .flatMap(comment -> comment.getLikes().stream())
            .anyMatch(like -> like.getUserId().equals(userId));
    }
    
    private void verifyUserDidntAlreadyLikedPost(LikeDto dto) {
        Long postId = dto.getPostId();
        Long userId = dto.getUserId();
        
        likeRepository.findByPostIdAndUserId(postId, userId).ifPresent(
            (like) -> throwAlreadyExistsException(String.format(
                "User with id %d already has like on post with Id %d",
                userId,
                postId
            )));
    }
    
    private Post getPostById(Long postId) {
        return postRepository.findById(postId).orElseThrow(
            () -> new NotFoundException(String.format("Post with id %d not found", postId))
        );
    }
}
