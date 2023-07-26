package faang.school.postservice.service;

import com.fasterxml.jackson.databind.cfg.ContextAttributes;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.util.validator.PostServiceValidator;
import net.bytebuddy.implementation.Implementation;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @Mock
    private PostServiceValidator validator;

    @Mock
    private PostRepository postRepository;

    @Spy
    private PostMapper postMapper;

    @InjectMocks
    private PostService postService;

    @BeforeEach
    void setUp() {
        Mockito.doNothing().when(validator).validatePost(Mockito.any());
    }

    @Test
    void addPostTest_InputsAreCorrect_ShouldGetCorrectPost() {
        PostDto postDto = buildPostDto();

        Assert.assertEquals(buildPost(), postMapper.toDto(postRepository.save(postMapper.toEntity(postDto))));
    }

    @Test
    void addPostTest_InputsAreIncorrect_ShouldThrowException() {
        PostDto postDto = buildPostDto();

        postService.addPost(postDto);

        Mockito.verify(postMapper, Mockito.times(1)).toDto(Mockito.any());
    }

    private PostDto buildPostDto() {
        return PostDto.builder()
                .content("content")
                .authorId(1L)
                .adId(1L)
                .build();
    }

    private Post buildPost() {
        return Post.builder()
                .id(0)
                .content("content")
                .authorId(1L)
                .ad(Ad.builder().id(1L).build())
                .likes(new ArrayList<>())
                .comments(new ArrayList<>())
                .albums(new ArrayList<>())
                .published(false)
                .deleted(false)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
