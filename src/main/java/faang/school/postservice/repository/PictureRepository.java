package faang.school.postservice.repository;

import faang.school.postservice.model.Picture;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PictureRepository extends JpaRepository<Picture, Long> {
    @Query("DELETE FROM Picture WHERE pictureName = :pictureName")
    void deletePictureByName(@Param("pictureName") String pictureName);
}
