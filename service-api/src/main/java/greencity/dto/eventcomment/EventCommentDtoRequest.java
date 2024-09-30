package greencity.dto.eventcomment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class EventCommentDtoRequest {
    @NotBlank(message = "The text of comment can not be empty")
    @Pattern(regexp = "^[a-zA-Z0-9!\"#$%&'()*+,-./:;<=>?@\\[\\]^_`{|}~\\s]+$",
            message = "Comment contains invalid characters.")
    @Length(min = 1, max = 8000)
    private String text;
    private Long parentCommentId;
}
