package faang.school.postservice.schedulers;

import com.google.common.collect.Lists;
import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.repository.ad.AdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AsyncAdRemovalService {

    private final AdRepository adRepository;

    @Async
    @Transactional
    public void processBatch(List<Ad> bath, int batchSize) {
        List<List<Ad>> chunks = Lists.partition(bath, batchSize);
        chunks.forEach(adRepository::deleteAll);
    }
}
