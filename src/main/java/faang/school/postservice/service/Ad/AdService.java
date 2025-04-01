package faang.school.postservice.service.Ad;

public interface AdService {

    void removeExpiredAds();

    void deleteAdById(long id);
}
