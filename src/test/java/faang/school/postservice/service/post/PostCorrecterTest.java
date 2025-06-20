//package faang.school.postservice.service.post;
//
//import faang.school.postservice.dto.languagetool.LanguageToolResponse;
//import faang.school.postservice.model.Post;
//import faang.school.postservice.repository.PostRepository;
//import faang.school.postservice.service.PostService;
//import faang.school.postservice.service.languagetool.LanguageToolService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//
//import java.util.List;
//import java.util.concurrent.CompletableFuture;
//
//import static org.junit.Assert.assertEquals;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.doReturn;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//
//class PostCorrecterTest {
//    private static final String LANGUAGE_TEST = "auto";
//
//    @Mock
//    private PostRepository postRepository;
//
//    @Mock
//    private LanguageToolService languageToolService;
//
//    @Mock
//    private PostService postService;
//
//    @InjectMocks
//    private PostCorrecter postCorrecter;
//
//    private Post postOne;
//    private Post postTwo;
//    private List<Post> posts;
//
//    @BeforeEach
//    void setUp() {
//        postOne = Post.builder()
//                .id(1L)
//                .content("TestContent")
//                .published(false)
//                .deleted(false)
//                .build();
//
//        postTwo = Post.builder()
//                .id(2L)
//                .content("TestContent")
//                .published(false)
//                .deleted(false)
//                .build();
//
//        posts = List.of(postOne, postTwo);
//    }
//
//
//    void testCorrectingSpellingOfPosts() {
//        when(postService.getAllUnpublishedPost()).thenReturn(posts);
//        doReturn(CompletableFuture.completedFuture(null))
//                .when(postCorrecter).correctingContentPost(postOne);
//        doReturn(CompletableFuture.completedFuture(null))
//                .when(postCorrecter).correctingContentPost(postTwo);
//
//        postCorrecter.correctingSpellingOfPosts();
//
//        verify(postService).getAllUnpublishedPost();
//        verify(postCorrecter).correctingContentPost(postOne);
//        verify(postCorrecter).correctingContentPost(postTwo);
//    }
//
//    void testCorrectingContentPost() {
//        String text = postOne.getContent();
//
//        LanguageToolResponse response = new LanguageToolResponse();
//        LanguageToolResponse.Match match = new LanguageToolResponse.Match();
//        match.setOffset(0);
//        match.setLength(text.length());
//        match.setReplacements(List.of(new LanguageToolResponse.Replacement("ReplacementsText")));
//        response.setMatches(List.of(match));
//
//        when(languageToolService.checkText(text, LANGUAGE_TEST))
//                .thenReturn(CompletableFuture.completedFuture(response));
//
//        CompletableFuture<Void> future = postCorrecter.correctingContentPost(postOne);
//        future.join();
//
//        verify(languageToolService).checkText(text, LANGUAGE_TEST);
//        verify(postRepository).save(postOne);
//        assertEquals("ReplacementsText", postOne.getContent());
//    }
//
//
//
//
//}