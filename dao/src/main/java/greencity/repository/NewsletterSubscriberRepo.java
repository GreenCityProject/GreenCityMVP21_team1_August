package greencity.repository;

import greencity.entity.NewsletterSubscriber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface NewsletterSubscriberRepo extends JpaRepository<NewsletterSubscriber, Long> {
    @Query("SELECT n FROM NewsletterSubscriber n WHERE n.email = ?1")
    NewsletterSubscriber findByEmail(String email);

    Boolean existsByEmail(String email);
}
