package faang.school.postservice.service;

import faang.school.postservice.dto.hashtag.HashtagDto;
import faang.school.postservice.mapper.HashtagMapper;
import faang.school.postservice.messaging.listening.HashtagListener;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class HashtagService {
    private final PostRepository postRepository;
    private final HashtagMapper hashtagMapper;

    @Transactional
    public void saveHashtags(Set<HashtagDto> hashtags){
        Long postId = hashtags.stream().findFirst().orElseThrow(EntityNotFoundException::new).getPostId();
        Post currentPost = postRepository.findById(postId).orElseThrow(() -> new EntityNotFoundException("There is no post with ID + " + postId));
        currentPost.setHashtags(hashtagMapper.setDtoToEntity(hashtags));
        postRepository.save(currentPost);
    }
}
