package faang.school.postservice.dto.user;

import faang.school.postservice.model.PreferredContact;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Builder
@Data
@AllArgsConstructor
public class UserDto {
   private long id;
   private String username;
   private String email;
   private String phone;
   private Long telegramId;
   private PreferredContact preference;;
   private List<Long> mentorIds;
   private List<Long> menteeIds;
}