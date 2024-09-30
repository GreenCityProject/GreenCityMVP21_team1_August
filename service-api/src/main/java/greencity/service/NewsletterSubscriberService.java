package greencity.service;

import greencity.dto.newslettersubscriber.NewsletterSubscriberDto;
import org.springframework.stereotype.Service;

@Service
public interface NewsletterSubscriberService {
    NewsletterSubscriberDto subscribe(NewsletterSubscriberDto newsletterSubscriberDto);

    NewsletterSubscriberDto findByEmail(String email);
}
