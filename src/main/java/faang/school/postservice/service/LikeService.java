package faang.school.postservice.service;

import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.exceptions.DataNotExistingException;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.LikeValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeValidator likeValidator;
    private final PostRepository postRepository;
    private final LikeMapper likeMapper;
    private final LikeRepository likeRepository;

    public LikeDto likePost(LikeDto likeDto) {
        likeValidator.validateLike(likeDto);
        Post post = postRepository.findById(likeDto.getPostId())
                .orElseThrow(() -> new DataNotExistingException(String
                        .format("Post with id:%d doesn't exist", likeDto.getPostId())));
        Like like = likeMapper.toModel(likeDto);
        like.setPost(post);
        likeRepository.save(like);
        return likeMapper.toDto(like);
    }
}
