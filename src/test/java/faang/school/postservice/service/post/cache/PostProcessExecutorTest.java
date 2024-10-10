package faang.school.postservice.service.post.cache;

import faang.school.postservice.dto.post.serializable.PostCacheDto;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static faang.school.postservice.util.post.PostCacheFabric.buildHashTags;
import static faang.school.postservice.util.post.PostCacheFabric.buildPostCacheDto;
import static faang.school.postservice.util.post.PostCacheFabric.buildPostCacheDtoWithTags;
import static faang.school.postservice.util.post.PostCacheFabric.buildPostCacheDtosWithTags;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PostProcessExecutorTest {
    private static final int NUMBER_OF_TAGS = 3;
    private static final int NUMBER_OF_POSTS = NUMBER_OF_TAGS;
    private static final long USER_ID = 1L;
    private static final String TAG = "java";
    private static final int START = 0;
    private static final int END = 9;

    @Mock
    private PostCacheOperations postCacheOperations;

    @InjectMocks
    private PostCacheProcessExecutor postCacheProcessExecutor;

    private final List<String> hashTags = buildHashTags(NUMBER_OF_TAGS);
    private final List<String> emptyHashTags = List.of();
    private final PostCacheDto postWithTags = buildPostCacheDtoWithTags(USER_ID, hashTags);
    private final PostCacheDto postEmptyTags = buildPostCacheDtoWithTags(USER_ID, emptyHashTags);
    private final PostCacheDto post = buildPostCacheDto(USER_ID);
    private final List<PostCacheDto> postsWithTags = buildPostCacheDtosWithTags(NUMBER_OF_POSTS);

//    @Test
//    @DisplayName("Given post with no new tags when check then will not add to cache")
//    void testNewPostProcessNoNewTags() {
//        postProcessExecutor.executeNewPostProcess(postEmptyTags);
//        verify(postCacheOperations, never()).addPostToCache(postEmptyTags, postEmptyTags.getHashTags());
//    }
//
//    @Test
//    @DisplayName("Given post with new tags and add post to cache")
//    void testNewPostProcessSuccessful() {
//        postProcessExecutor.executeNewPostProcess(postWithTags);
//        verify(postCacheOperations).addPostToCache(postWithTags, postWithTags.getHashTags());
//    }
//
//    @Test
//    @DisplayName("Given post with no primal tags when check then will not delete from cache")
//    void testDeletePostProcessNoPrimalTags() {
//        postProcessExecutor.executeDeletePostProcess(post, emptyHashTags);
//        verify(postCacheOperations, never()).deletePostOfCache(post, emptyHashTags);
//    }
//
//    @Test
//    @DisplayName("Given post with primal tags and delete from cache")
//    void testDeletePostProcessSuccessful() {
//        postProcessExecutor.executeDeletePostProcess(post, hashTags);
//        verify(postCacheOperations).deletePostOfCache(post, hashTags);
//    }
//
//    @Test
//    @DisplayName("Update post process no tags post")
//    void testUpdatePostProcessNothingToDo() {
//        postProcessExecutor.executeUpdatePostProcess(postEmptyTags, emptyHashTags);
//
//        verify(postCacheOperations, never()).addPostToCache(postEmptyTags, emptyHashTags);
//        verify(postCacheOperations, never()).deletePostOfCache(postEmptyTags, emptyHashTags);
//        verify(postCacheOperations, never()).updatePostOfCache(post, emptyHashTags, emptyHashTags);
//    }
//
//    @Test
//    @DisplayName("Given empty primal and ful upd tags and add post to cache")
//    void testUpdatePostProcessAddPostToCache() {
//        postProcessExecutor.executeUpdatePostProcess(postWithTags, emptyHashTags);
//
//        verify(postCacheOperations).addPostToCache(postWithTags, postWithTags.getHashTags());
//    }
//
//    @Test
//    @DisplayName("Given ful primal and empty upd tags and delete post from cache")
//    void testUpdatePostProcessDelete() {
//        postProcessExecutor.executeUpdatePostProcess(postEmptyTags, hashTags);
//
//        verify(postCacheOperations).deletePostOfCache(postEmptyTags, hashTags);
//    }
//
//    @Test
//    @DisplayName("Given ful primal and ful upd tags and update post in cache")
//    void testUpdatePostProcessUpdate() {
//        postProcessExecutor.executeUpdatePostProcess(postWithTags, hashTags);
//
//        verify(postCacheOperations).updatePostOfCache(postWithTags, hashTags, postWithTags.getHashTags());
//    }
//
//    @Test
//    @DisplayName("Given list of posts and add each to cache by tag to find")
//    void testAddListOfPostsToCacheSuccessful() {
//        postProcessExecutor.executeAddListOfPostsToCache(postsWithTags, TAG);
//
//        verify(postCacheOperations, times(postsWithTags.size()))
//                .addPostToCacheByTag(any(PostCacheDto.class), anyList(), anyString());
//    }
//
//    @Test
//    @DisplayName("Given ")
//    void testFindInRangeByHashTagSuccessful() {
//        postProcessExecutor.findInRangeByHashTag(TAG, START, END);
//
//        verify(postCacheOperations).findIdsByHashTag(TAG, START, END);
//        verify(postCacheOperations).findAllByIds(anyList());
//    }
}