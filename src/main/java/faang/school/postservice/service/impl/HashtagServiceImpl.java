package faang.school.postservice.service.impl;

import faang.school.postservice.model.dto.HashtagDto;
import faang.school.postservice.model.dto.PostDto;
import faang.school.postservice.mapper.HashtagMapper;
import faang.school.postservice.model.entity.Hashtag;
import faang.school.postservice.model.entity.Post;
import faang.school.postservice.repository.HashtagRepository;
import faang.school.postservice.service.HashtagService;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class HashtagServiceImpl implements HashtagService {
    private final HashtagRepository hashtagRepository;
    private final HashtagMapper hashtagMapper;
    private final PostService postService;

    @Override
    @Transactional
    public void process(PostDto postDto) {
        saveHashtags(findHashtags(postDto.getContent(), postDto.getId()));
    }

    private Set<HashtagDto> findHashtags(String content, Long postId) {
        List<String> hashtags = new ArrayList<>();
        Pattern pattern = Pattern.compile("#\\S+");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            hashtags.add(matcher.group());
        }
        return hashtags.stream()
                .distinct()
                .map(hashtag -> HashtagDto.builder().postId(postId).content(hashtag).build())
                .collect(Collectors.toSet());
    }

    private void saveHashtags(Set<HashtagDto> hashtags) {
        if (hashtags.isEmpty()) {
            return;
        }

        Long postId = hashtags.iterator().next().getPostId();
        Post post = postService.getPostByIdInternal(postId);
        Set<Hashtag> hashtagEntities = hashtags.stream()
                .map(hashtagDto -> hashtagRepository.save(hashtagMapper.dtoToEntity(hashtagDto)))
                .collect(Collectors.toSet());

        post.getHashtags().addAll(hashtagEntities);
        postService.updatePostInternal(post);
    }
}
