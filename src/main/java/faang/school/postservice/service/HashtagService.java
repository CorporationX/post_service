package faang.school.postservice.service;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.HashtagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HashtagService {
    private final HashtagRepository hashtagRepository;
    private final PostMapper postMapper;

    public Set<PostDto> getPostByHashtag(String hashtag) {
        Set<Post> uniquePosts = new LinkedHashSet<>();
        hashtagRepository.findByHashtags(hashtag.toLowerCase())
                .forEach(hashtagEntity -> uniquePosts.addAll(hashtagEntity.getPosts()));

        return uniquePosts.stream()
                .sorted(Comparator.comparing(Post::getCreatedAt))
                .map(postMapper::toDto)
                .collect(Collectors.toSet());
    }
}
