package faang.school.postservice.moderation.moderator.dictionary;

import faang.school.postservice.dictionary.ModerationDictionary;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;


import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = ModerationDictionary.class)
class ModerationDictionaryTest {
    @Autowired
    ModerationDictionary moderationDictionary;
    @MockBean
    CommentRepository commentRepository;


    @Test
    void verifyCommentTestFalse() throws IOException {
        Comment unverifiedComment = new Comment();
        Set<String> forbiddenWords = new HashSet<>();
        String a = "A";
        String b = "B";
        forbiddenWords.add(a);
        forbiddenWords.add(b);
        unverifiedComment.setVerified(false);
        unverifiedComment.setContent(a);


        moderationDictionary.verifyComment(unverifiedComment);

        verify(commentRepository, times(1)).save(unverifiedComment);
    }

    @Test
    void verifyCommentTestTrue() throws IOException {
        Comment unverifiedComment = new Comment();
        Set<String> forbiddenWords = new HashSet<>();
        String a = "A";
        String b = "B";
        forbiddenWords.add(a);
        unverifiedComment.setVerified(false);
        unverifiedComment.setContent(b);

        moderationDictionary.verifyComment(unverifiedComment);

        verify(commentRepository, times(1)).save(unverifiedComment);
    }
}

