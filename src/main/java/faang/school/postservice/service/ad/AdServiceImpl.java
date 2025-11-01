package faang.school.postservice.service.ad;

import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.repository.ad.AdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AdServiceImpl implements AdService {
    private final AdRepository adRepository;
    private final ProcessingAd processingAd;

    @Value("${post.ad.batch-size}")
    private int SIZE;

    @Override
    public void removeAd() {
        int page = 0;
        Page<Ad> adPage;
        do {
            adPage = adRepository.findAll(PageRequest.of(page, SIZE, Sort.by("endDate")));
            if (!adPage.isEmpty()) {
                processingAd.processingAd(adPage.getContent());
            }
            page++;
        } while (adPage.hasNext());
    }
}