package greencity.dto.event;

import greencity.constant.ServiceValidationConstants;
import greencity.dto.user.AuthorDto;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
public class EventCreationDto {
    @Size(min = 1, max = 70, message = ServiceValidationConstants.EVENT_TITLE_LENGTH)
    private String eventTitle;

    @Size(min = 20, max = 63206, message = ServiceValidationConstants.EVENT_DESCRIPTION_LENGTH)
    private String description;

    @Size(min = 1, max = 7, message = ServiceValidationConstants.EVENT_AMOUNT_OF_DAYS)
    private Set<EventDayDetailsDto> eventDayDetailsList;

    private String eventType;

    private AuthorDto author;
}
