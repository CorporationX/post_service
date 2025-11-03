package faang.school.postservice.service;

import faang.school.postservice.dto.HashTag.HashTagDto;
import faang.school.postservice.dto.Post.PostDto;
import faang.school.postservice.mapper.HashTagMapper;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.HashTag;
import faang.school.postservice.repository.HashTagRepository;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HashTagService {
    private final HashTagRepository hashTagRepository;
    private final PostRepository postRepository;
    private final PostMapper postMapper;

    public List<PostDto> getPostsByHashTag(String hashTagName) {
        HashTag hashTag = hashTagRepository.getHashTagByName(hashTagName);
        return postMapper.toPostsDto(
            postRepository.findPostsByHashTag(
                    hashTag.getId()
            )
        );
    }
}
