package faang.school.postservice.service;

import faang.school.postservice.model.ad.Ad;

import java.util.List;

public interface AdService {
    void removeExpiredAds(int maxListSize);

    void deleteExpiredAdsByBatch(List<Ad> ads);
}
