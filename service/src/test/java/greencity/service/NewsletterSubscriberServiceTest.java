package greencity.service;

import greencity.dto.newslettersubscriber.NewsletterSubscriberDto;
import greencity.entity.NewsletterSubscriber;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.EmailNotFoundException;
import greencity.repository.NewsletterSubscriberRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NewsletterSubscriberServiceTest {

    @InjectMocks
    private NewsletterSubscriberServiceImpl newsletterSubscriberServiceimpl;

    @Mock
    private NewsletterSubscriberRepo newsletterSubscriberRepo;

    @Mock
    private ModelMapper modelMapper;

    private NewsletterSubscriber newsletterSubscriber;
    private NewsletterSubscriberDto newsletterSubscriberDto;

    @BeforeEach
    void setup() {
        newsletterSubscriber = new NewsletterSubscriber();
        newsletterSubscriber.setEmail("test@example.com");
        newsletterSubscriberDto = new NewsletterSubscriberDto();
        newsletterSubscriberDto.setEmail("test@example.com");
    }

    @Test
    void newsletterSubscriberService_subscribe() {

        when(modelMapper.map(newsletterSubscriberDto, NewsletterSubscriber.class)).thenReturn(newsletterSubscriber);
        when(newsletterSubscriberRepo.save(newsletterSubscriber)).thenReturn(newsletterSubscriber);
        when(modelMapper.map(newsletterSubscriber, NewsletterSubscriberDto.class)).thenReturn(newsletterSubscriberDto);

        NewsletterSubscriberDto result = newsletterSubscriberServiceimpl.subscribe(newsletterSubscriberDto);

        assertEquals(newsletterSubscriberDto, result);
        verify(newsletterSubscriberRepo).save(newsletterSubscriber);
    }

    @Test
    void newsletterSubscriberService_findByEmail() {
        when(newsletterSubscriberRepo.existsByEmail(newsletterSubscriberDto.getEmail())).thenReturn(true);
        when(newsletterSubscriberRepo.findByEmail(newsletterSubscriberDto.getEmail())).thenReturn(newsletterSubscriber);
        when(modelMapper.map(newsletterSubscriber, NewsletterSubscriberDto.class)).thenReturn(newsletterSubscriberDto);

        NewsletterSubscriberDto result = newsletterSubscriberServiceimpl.findByEmail(newsletterSubscriberDto.getEmail());

        assertEquals(newsletterSubscriberDto, result);
        verify(newsletterSubscriberRepo).existsByEmail(newsletterSubscriberDto.getEmail());
        verify(newsletterSubscriberRepo).findByEmail(newsletterSubscriberDto.getEmail());
    }

    @Test
    void newsletterSubscriberService_subscribe_existingEmail() {
        when(newsletterSubscriberRepo.existsByEmail(newsletterSubscriberDto.getEmail())).thenReturn(true);
        assertThrows(BadRequestException.class, () -> newsletterSubscriberServiceimpl.subscribe(newsletterSubscriberDto));
    }

    @Test
    void newsletterSubscriberService_findByEmail_notFound() {
        when(newsletterSubscriberRepo.existsByEmail("nonexistent@example.com")).thenReturn(false);

        assertThrows(EmailNotFoundException.class, () -> newsletterSubscriberServiceimpl.findByEmail("nonexistent@example.com"));
        verify(newsletterSubscriberRepo).existsByEmail("nonexistent@example.com");
    }

    @Test
    void newsletterSubscriberService_findByEmail_nullInput() {
        assertThrows(IllegalArgumentException.class, () -> newsletterSubscriberServiceimpl.findByEmail(null));
    }
}
