package greencity.dto.notification;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class NotificationDto {
    private Long id;
    private String section;
    private String sectionType;
    private String text;
    private boolean isRead;
    private Long userId;
}