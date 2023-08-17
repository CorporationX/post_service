package faang.school.postservice.service.moderation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ModerationDictionaryTest {

    @InjectMocks
    private ModerationDictionary moderationDictionary;

    @Test
    void testCheckWordContent() {
        ReflectionTestUtils.setField(moderationDictionary, "obsceneWordsDictionary", getListOfObsceneWords());
        boolean actual1 = moderationDictionary.checkWordContent("you are stupid nigga");
        boolean actual2 = moderationDictionary.checkWordContent("this is good content");
        boolean actual3 = moderationDictionary.checkWordContent("A_N.`A#L in this post");
        boolean actual4 = moderationDictionary.checkWordContent("you can take p.o.r.n.o in this post");
        boolean actual5 = moderationDictionary.checkWordContent("here s_w_a_s_ti`k~a");

        assertEquals(true, actual1);
        assertEquals(false, actual2);
        assertEquals(true, actual3);
        assertEquals(true, actual4);
        assertEquals(true, actual5);
    }

    private Set<String> getListOfObsceneWords() {
        return Set.of("nigga", "anal", "porno", "swastika");
    }
}
