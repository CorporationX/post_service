package faang.school.postservice.service;

import faang.school.postservice.dto.PostDto;
import faang.school.postservice.dto.hashtag.HashtagDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.HashtagMapper;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.HashtagRepository;
import faang.school.postservice.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HashtagService {
    private final HashtagRepository hashtagRepository;
    private final PostRepository postRepository;
    private final HashtagMapper hashtagMapper;
    private final PostMapper postMapper;

    @Transactional
    public HashtagDto addHashtagToPost(HashtagDto hashtagDto, Long currentUserId){
        validateCurrentUser(hashtagDto.getPostId(), currentUserId);
        return hashtagMapper.entityToDto(hashtagRepository.save(hashtagMapper.dtoToEntity(hashtagDto)));
    }

    public List<PostDto> getPostsByHashtag(HashtagDto hashtagDto){
        List<Post> sortedByDate = postRepository.findByHashtagId(hashtagDto.getId()).stream()
                .sorted(Comparator.comparing(Post::getCreatedAt))
                .toList();
        return postMapper.listEntityToDto(sortedByDate);
    }

    private void validateCurrentUser(Long postId, Long currentUserId){
        Post currentPost = postRepository.findById(postId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Post with ID %d doesn't exist", postId)));
        if(!(currentPost.getAuthorId().equals(currentUserId))){
            throw new DataValidationException("Current user can't add hashtag to post with ID " + postId);
        }
    }
}
