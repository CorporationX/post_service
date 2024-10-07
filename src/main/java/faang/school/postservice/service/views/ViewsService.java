package faang.school.postservice.service.views;

import faang.school.postservice.producer.views.ViewsServiceProducer;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ViewsService {

    private final PostRepository postRepository;
    private final List<ViewsServiceProducer> producers;
    
    @Transactional
    public void viewPost(long postId) {
        postRepository.incrementViewsByPostId(postId);

        for(var producer: producers) {
            producer.send(postId);
        }
    }
}
