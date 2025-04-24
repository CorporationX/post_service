package faang.school.postservice.service.event;

import faang.school.postservice.model.event.Event;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class PostViewEventBufferTest {

    private PostViewEventBuffer buffer = new PostViewEventBuffer();

    @Test
    void testAddAndFlush() {
        Event event = mock(Event.class);
        buffer.add(event);

        List<Event> events = buffer.flush();

        assertEquals(1, events.size());
        assertEquals(event, events.get(0));
        assertTrue(buffer.flush().isEmpty());
    }
}
