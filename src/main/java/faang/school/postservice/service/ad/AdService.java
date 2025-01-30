package faang.school.postservice.service.ad;

import faang.school.postservice.model.ad.Ad;

import java.util.List;

public interface AdService {
    void deleteAds(List<Ad> ads);
}