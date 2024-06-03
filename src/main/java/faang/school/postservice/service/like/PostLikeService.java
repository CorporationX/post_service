package faang.school.postservice.service.like;

import org.springframework.stereotype.Service;

import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.exception.NotFoundException;
import faang.school.postservice.mapper.like.LikeMapper;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validation.like.post.PostLikeValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostLikeService {
    private final PostLikeValidator validator;
    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final LikeMapper mapper;
    
    public LikeDto likePost(LikeDto dto) {
        validator.verifyCanLikePost(dto);
        
        Like like = mapper.toModel(dto);
        like.setPost(getPostById(dto.getPostId()));
        likeRepository.save(like);
        
        return mapper.toDto(like);
    }
    
    public void deletePostLike(@Valid LikeDto dto) {
        Long userId = dto.getUserId();
        validator.verifyLikeExists(userId);
        
        likeRepository.deleteByPostIdAndUserId(dto.getPostId(), userId);
    }
 
    //TODO: Заменить на использование сервиса, когда зальют в мастер
    private Post getPostById(Long postId) {
        return postRepository.findById(postId).orElseThrow(
            () -> new NotFoundException(String.format("Post with id %d not found", postId))
        );
    }
}
