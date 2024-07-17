package faang.school.postservice.moderation;

import faang.school.postservice.dto.moderation.ModerationDictionary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.*;

class ModerationDictionaryTest {
    private ModerationDictionary moderationDictionary;

    @BeforeEach
    void setUp() {
        moderationDictionary = new ModerationDictionary(Set.of("bad"));
    }

    @Test
    public void whenInspectThenReturnTrue() {
        assertThat(moderationDictionary.inspect("you are bad")).isTrue();
    }

    @Test
    public void whenInspectThenReturnFalse() {
        assertThat(moderationDictionary.inspect("you are good")).isFalse();
    }
}