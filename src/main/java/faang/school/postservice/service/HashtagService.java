package faang.school.postservice.service;

import faang.school.postservice.model.dto.hashtag.HashtagDto;
import faang.school.postservice.mapper.HashtagMapper;
import faang.school.postservice.model.Hashtag;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.HashtagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class HashtagService {
    private final HashtagRepository hashtagRepository;
    private final HashtagMapper hashtagMapper;
    private final PostService postService;

    @Transactional
    public void saveHashtags(Set<HashtagDto> hashtags) {
        Post post = new Post();
        for (var hashtagDto : hashtags) {
            Hashtag hashtag = hashtagRepository.save(hashtagMapper.dtoToEntity(hashtagDto));
            post = postService.getPostByIdInternal(hashtagDto.getPostId());
            post.getHashtags().add(hashtag);
        }
        postService.updatePostInternal(post);
    }
}
