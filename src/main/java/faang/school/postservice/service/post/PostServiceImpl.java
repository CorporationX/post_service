package faang.school.postservice.service.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.hashtag.HashtagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final HashtagService hashtagService;

    //this is temp method
    @Override
    public PostDto activate(PostDto postDto) {
        Post post = postRepository.findById(postDto.id())
                .orElseThrow(() -> new NoSuchElementException("Post not found with id: " + postDto.id()));

        post.setPublished(true);
        postRepository.save(post);
        hashtagService.createHashtags(post); //just need to insert this row

        return PostDto.builder().build();
    }

    //this is temp method
    @Override
    public PostDto updatePost(PostDto postDto) {
        Post post = postRepository.findById(postDto.id())
                .orElseThrow(() -> new NoSuchElementException("Post not found with id: " + postDto.id()));

//        postValidator.updatePostValidator(post, postDto);

//        post.setUpdatedAt(LocalDateTime.now());
        post.setContent("Editted content left only hashtag #post and no hshtg");
        postRepository.save(post);
        hashtagService.updateHashtags(post); //just need to insert this row

        return postMapper.toDto(postRepository.save(post));
    }

    @Override
    public List<PostDto> getPostsByHashtag(String hashtag) {
        List<Post> posts = hashtagService.findPostsByHashtag(hashtag);
        List<PostDto> postsDto = postMapper.toDtoList(posts);

        postsDto.sort(Comparator.comparing(PostDto::publishedAt).reversed());

        return postsDto;
    }
}
