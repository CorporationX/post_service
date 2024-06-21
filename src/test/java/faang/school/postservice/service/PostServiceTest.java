package faang.school.postservice.service;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Hashtag;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.VerifyStatus;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.PostValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    private static final Long POST_ID = 1L;
    private static final String CONTENT = "#Java oop cross platform #language with wide #possibilities";
    private static final String HASHTAG = "language";

    @Mock
    private PostRepository postRepository;

    @Mock
    private ModerationDictionary moderationDictionary;

    @Mock
    private HashtagService hashtagService;

    @Mock
    private PostMapper postMapper;

    @Mock
    private PostValidator postValidator;

    @InjectMocks
    private PostService postService;

    Post post;
    PostDto postDto;
    PostDto savedPostDto;
    Post newPost;
    Post savedPost;
    Hashtag hashtag;

    @BeforeEach
    public void init() {
        post = Post.builder()
                .content("Content")
                .build();
        postDto = PostDto.builder()
                .content(CONTENT)
                .build();

        newPost = Post.builder()
                .content(CONTENT)
                .build();

        savedPost = Post.builder()
                .id(POST_ID)
                .content(CONTENT)
                .build();

        savedPostDto = PostDto.builder()
                .id(POST_ID)
                .content(CONTENT)
                .build();
        hashtag = Hashtag.builder()
                .name(HASHTAG)
                .build();
    }

    @Test
    @DisplayName("Moderate posts when true")
    public void moderatePostsTrue() {
        when(postRepository.findNotVerifiedPosts()).thenReturn(List.of(post));
        when(moderationDictionary.checkString(post.getContent())).thenReturn(true);

        postService.moderateAll();

        assertEquals(VerifyStatus.VERIFIED, post.getVerifyStatus());
    }

    @Test
    @DisplayName("Moderate posts when false")
    public void moderatePostsFalse() {
        when(postRepository.findNotVerifiedPosts()).thenReturn(List.of(post));
        when(moderationDictionary.checkString(post.getContent())).thenReturn(false);

        postService.moderateAll();

        assertEquals(VerifyStatus.NOT_VERIFIED, post.getVerifyStatus());
    }

    @Test
    public void whenCreatePost() {
        when(postMapper.toEntity(postDto)).thenReturn(newPost);
        when(postMapper.toDto(savedPost)).thenReturn(savedPostDto);
        when(postRepository.saveAndFlush(newPost)).thenReturn(savedPost);

        PostDto actualResult = postService.create(postDto);

        verify(hashtagService).parsePostAndCreateHashtags(savedPost);
        assertEquals(savedPostDto, actualResult);
    }

    @Test
    public void whenFindByHashtag() {
        List<PostDto> expectedResult = List.of(savedPostDto);
        List<Post> expectedPostList = List.of(savedPost);
        hashtag.setPosts(expectedPostList);

        when(hashtagService.findByName(HASHTAG)).thenReturn(hashtag);
        when(postMapper.toListDto(expectedPostList)).thenReturn(expectedResult);

        List<PostDto> actualResult = postService.findByHashtag(HASHTAG);

        assertIterableEquals(expectedResult, actualResult);
        assertEquals(actualResult.get(0), expectedResult.get(0));
    }
}