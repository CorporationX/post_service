package faang.school.postservice.service;

import faang.school.postservice.model.dto.ad.AdDto;

public interface AdService {

    void removeExpiredAds(int maxListSize);

    AdDto buyAd(AdDto dto);
}
