package faang.school.postservice.service;

import faang.school.postservice.model.ad.Ad;

import java.util.List;

public interface AdServiceAsync {

    void deleteExpiredAdsByBatch(List<Ad> ads);
}
