package faang.school.postservice.service;

import faang.school.postservice.corrector.ContentCorrector;
import faang.school.postservice.service.post.PostService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {
    @Mock
    private ContentCorrector contentCorrector;
    @InjectMocks
    private PostService postService;

    @Test
    public void test_correctionTextInPost_Successful() {
        Long postId = 1L;

        Assertions.assertDoesNotThrow(() -> postService.correctionTextInPost(postId));
        Mockito.verify(contentCorrector).spellCheckPostById(postId);
    }
}
