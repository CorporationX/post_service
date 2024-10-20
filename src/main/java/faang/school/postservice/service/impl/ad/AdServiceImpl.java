package faang.school.postservice.service.impl.ad;

import faang.school.postservice.mapper.ad.AdMapper;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.dto.ad.AdDto;
import faang.school.postservice.model.dto.post.PostDto;
import faang.school.postservice.model.entity.Ad;
import faang.school.postservice.model.entity.Post;
import faang.school.postservice.repository.ad.AdRepository;
import faang.school.postservice.service.AdService;
import faang.school.postservice.service.AdServiceAsync;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.ListUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AdServiceImpl implements AdService {

    private final AdRepository adRepository;
    private final AdServiceAsync adServiceAsync;
    private final AdMapper adMapper;
    private final PostMapper postMapper;
    private final PostService postService;

    @Override
    public void removeExpiredAds(int batchSize) {
        var ads = adRepository.findAllByEndDateBefore(LocalDateTime.now());
        if (!ads.isEmpty()) {
            ListUtils.partition(ads, batchSize).forEach(adServiceAsync::deleteExpiredAdsByBatch);
        }
    }

    @Override
    @Transactional
    public AdDto buyAd(AdDto dto) {
        Ad ad = adMapper.toEntity(dto);
        PostDto postDto = postService.getPost(dto.postId());
        Post post = postMapper.toEntity(postDto);
        ad.setPost(post);
        Ad savedAd = adRepository.save(ad);
        return adMapper.toDto(savedAd);
    }
}
