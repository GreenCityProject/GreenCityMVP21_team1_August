package greencity.dto.newslettersubscriber;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class NewsletterSubscriberDto {
    private Long id;
    private String email;
}
