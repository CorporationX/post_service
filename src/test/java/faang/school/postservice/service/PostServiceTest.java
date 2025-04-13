package faang.school.postservice.service;

import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostService postService;

    @Test
    void moderateAllUnverifiedPostsSuccessWithEmptyList() {

        when(postRepository.findByVerifiedAtIsNull()).thenReturn(Collections.emptyList());

        postService.moderateAllUnverifiedPosts();

        verify(postRepository, times(1)).findByVerifiedAtIsNull();
        verify(postRepository, never()).saveAll(any());
    }

}
