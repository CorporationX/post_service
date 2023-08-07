package faang.school.postservice.service;

import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.validator.LikeValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeValidator likeValidator;
    private final LikeMapper likeMapper;
    private final LikeRepository likeRepository;
    private final PostService postService;

    public LikeDto likePost(LikeDto likeDto) {
        likeValidator.validateLike(likeDto);
        Long postId = likeDto.getPostId();
        Long userId = likeDto.getUserId();
        Post post = postService.getPost(likeDto.getPostId());
        Optional<Like> existingLike = likeRepository.findByPostIdAndUserId(postId, userId);
        if (existingLike.isPresent()) {
            return likeMapper.toDto(existingLike.get());
        }
        Like like = likeMapper.toModel(likeDto);
        like.setPost(post);
        likeRepository.save(like);
        return likeMapper.toDto(like);
    }

    public void unlikePost(long postId, long userId) {
        likeRepository.deleteByPostIdAndUserId(postId, userId);
    }
}
