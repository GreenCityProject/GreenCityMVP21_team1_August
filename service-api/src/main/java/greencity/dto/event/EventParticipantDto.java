package greencity.dto.event;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class EventParticipantDto {
    private Long id;
    private EventDto event;
    private Long userId;
    private LocalDateTime joinedAt;
    private String eventRole;
}