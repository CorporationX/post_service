package faang.school.postservice.controller;

import faang.school.postservice.service.CommentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class CommentControllerTest {
    @Mock
    private CommentService service;
    @InjectMocks
    private CommentController controller;


    @Test
    void testCreate() {
        controller.create();
        verify(service).create();
    }
}