package faang.school.postservice.service;

import faang.school.postservice.model.entity.Ad;

import java.util.List;

public interface AdServiceAsync {

    void deleteExpiredAdsByBatch(List<Ad> ads);
}
