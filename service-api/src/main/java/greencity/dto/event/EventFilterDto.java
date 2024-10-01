package greencity.dto.event;

import greencity.dto.enums.EventLine;
import greencity.dto.enums.EventTime;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
public class EventFilterDto {
    private EventLine eventLine;
    private String eventLocation;
    private EventTime eventTime;
}
