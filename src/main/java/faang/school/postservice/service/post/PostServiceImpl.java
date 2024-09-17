package faang.school.postservice.service.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.hashtag.HashtagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final HashtagService hashtagService;

    //this is temp service
    public PostDto activate(PostDto postDto) {
        Post post = postRepository.findById(postDto.id())
                .orElseThrow(() -> new NoSuchElementException("Post not found with id: " + postDto.id()));

        post.setPublished(true);

        hashtagService.createHashtags(post); //все ради этой строчки
        //return postMapper.toDto(postRepository.save(post));

        return PostDto.builder().build();
    }

    @Override
    public List<PostDto> getPostsByHashtag(String hashtag) {
        List<Post> posts = hashtagService.findPostsByHashtag(hashtag);
        List<PostDto> postsDto = postMapper.toDtoList(posts);

        postsDto.sort(Comparator.comparing(PostDto::publishedAt).reversed());

        return postsDto;
    }
}
