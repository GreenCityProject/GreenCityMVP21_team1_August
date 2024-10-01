package greencity.dto.eventcomment;

import greencity.dto.user.PlaceAuthorDto;
import lombok.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class EventCommentNotificationDto {
    private String eventTitle;
    private String  commentText;
    private String commentAuthor;
    private String commentDate;
    private PlaceAuthorDto author;
    private String secureToken;

}
