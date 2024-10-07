package greencity.dto.econews;

import lombok.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class AddEcoNewsDtoRequest {
    @NotEmpty
    private List<String> tags;

    @NotEmpty
    @Size(min = 20, max = 63206)
    private String text;

    @NotEmpty
    @Size(min = 1, max = 170)
    private String title;

    private String source;
}
