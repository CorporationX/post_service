package faang.school.postservice.service;

import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.moderation.ModerationDictionary;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.concurrent.Executors;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @InjectMocks
    private PostService postService;
    @Mock
    private PostRepository postRepository;
    @Mock
    private PostMapper postMapper;
    @Mock
    private ModerationDictionary moderationDictionary;

    private static final long ID_ONE = 1L;
    private static final long ID_TWO = 2L;
    private static final String CONTENT = "content";
    private static final String SWEAR_CONTENT = "bug";
    private static final int THREAD_COUNT = 5;
    private static final long SUBLIST_LENGTH = 10L;

    private Post firstPost;
    private Post secondPost;
    private Post verifiedFirstPost;
    private Post verifiedSecondPost;
    private List<Post> unverifiedPosts;
    private List<Post> verifiedPosts;

    @BeforeEach
    public void init() {
        ReflectionTestUtils.setField(postService, "sublistLength", SUBLIST_LENGTH);
        ReflectionTestUtils.setField(postService, "executor", Executors.newFixedThreadPool(THREAD_COUNT));

        firstPost = Post.builder()
                .id(ID_ONE)
                .content(CONTENT)
                .build();

        secondPost = Post.builder()
                .id(ID_TWO)
                .content(SWEAR_CONTENT)
                .build();

        verifiedFirstPost = Post.builder()
                .id(ID_ONE)
                .content(CONTENT)
                .verified(true)
                .build();

        verifiedSecondPost = Post.builder()
                .id(ID_TWO)
                .content(SWEAR_CONTENT)
                .verified(false)
                .build();

        unverifiedPosts = List.of(firstPost, secondPost);
        verifiedPosts = List.of(verifiedFirstPost, verifiedSecondPost);
    }

    @Test
    @DisplayName("Успешный вызов метода moderationPostContent")
    public void whenModeratePostsContentThenSuccess() {
        when(postRepository.findReadyToVerified()).thenReturn(unverifiedPosts);
        when(moderationDictionary.searchSwearWords(unverifiedPosts)).thenReturn(verifiedPosts);
        when(postRepository.saveAll(verifiedPosts)).thenReturn(verifiedPosts);

        postService.moderatePostsContent();

        verify(postRepository).findReadyToVerified();
        verify(moderationDictionary).searchSwearWords(unverifiedPosts);
        verify(postRepository).saveAll(anyList());
    }
}