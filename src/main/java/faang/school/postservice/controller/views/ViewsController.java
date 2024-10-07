package faang.school.postservice.controller.views;

import faang.school.postservice.service.views.ViewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/views")
@RequiredArgsConstructor
public class ViewsController {

    private final ViewsService viewsService;

    @PostMapping("/{postId}")
    public void viewPost(@PathVariable long postId) {
        viewsService.viewPost(postId);
    }
}
