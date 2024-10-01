package greencity.dto.user.friends;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode
public class FriendCardDtoResponse {
    @NotEmpty
    private Long id;
    @NotEmpty
    private String name;
    private String profilePicturePath;
    private double personalRate;
    private String city;
    private int mutualFriends;
}
