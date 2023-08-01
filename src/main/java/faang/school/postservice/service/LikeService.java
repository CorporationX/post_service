package faang.school.postservice.service;

import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final LikeMapper likeMapper;

    public LikeDto addLikeToPost(long postId, LikeDto like){
        Like likeEntity = likeMapper.toEntity(like);
        List<Like> likesOnPost = likeRepository.findByPostId(postId);
        likesOnPost.add(likeEntity);
        return likeMapper.toDto();
    }

    public LikeDto addLikeToComment(long commentId,LikeDto like){
        return null;
    }

    public void deleteLikeFromPost(long userId,long postId){

    }

    public void deleteLikeFromComment(long userId,long postId){

    }
}
