package greencity.dto.search;

import greencity.dto.event.EventAuthorDto;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class SearchEventDto {
    @NotEmpty
    private Long id;
    @NotEmpty
    private String title;
    @NotEmpty
    private EventAuthorDto author;
    @NotEmpty
    private LocalDate creationDate;
    @NotEmpty
    private List<String> tags;
}
