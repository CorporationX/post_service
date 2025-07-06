package faang.school.postservice.moderation.model.post;

import faang.school.postservice.config.moderation.ModerationDictionary;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@RequiredArgsConstructor
@Component
public class PostContentChecker {
    private final PostService postService;
    private final ModerationDictionary moderationDictionary;
    @Transactional
    public List<Post> verifyPostsBatch(List<Post> postList) {
        List<Post> verifiedPostList = new ArrayList<>();
        for (Post post : postList) {
            String content = post.getContent();
            boolean containsBadWord = moderationDictionary.containsProfanity(content);
            if (containsBadWord) {
                throw new DataValidationException("This post contains censorship");
            }
            post.setVerified(true);
            post.setVerifiedDate(LocalDateTime.now());
            postService.updatePost(post.getId(), post);
            verifiedPostList.add(post);
        }
        return verifiedPostList;
    }
}
