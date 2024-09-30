package greencity.dto.eventcomment;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventCommentDtoResponse {
    private Long id;
    private Long eventId;
    private Long userId;
    private String userName;
    private String text;
    private LocalDateTime createdDate;
    private boolean edited;
}
