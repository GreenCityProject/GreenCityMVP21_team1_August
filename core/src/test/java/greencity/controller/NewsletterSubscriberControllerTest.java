package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.dto.newslettersubscriber.NewsletterSubscriberDto;
import greencity.service.NewsletterSubscriberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import org.springframework.validation.Validator;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class NewsletterSubscriberControllerTest {
    @InjectMocks
    private NewsletterSubscriberController newsletterSubscriberController;

    @Mock
    private NewsletterSubscriberService newsletterSubscriberService;

    @Mock
    private Validator mockValidator;

    private MockMvc mockMvc;
    private static final String newsletterSubscriberControllerLink = "/newsletter-subscribers";
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(newsletterSubscriberController)
                .setValidator(mockValidator)
                .build();

        this.objectMapper = new ObjectMapper();
    }

    @Test
    void newsletterSubscriber_subscribe_statusIsOk() throws Exception {

        NewsletterSubscriberDto newsletterSubscriberDto = new NewsletterSubscriberDto();
        newsletterSubscriberDto.setEmail("test@gmail.com");

        when(newsletterSubscriberService.subscribe(any(NewsletterSubscriberDto.class))).thenReturn(newsletterSubscriberDto);

        String dtoJson = objectMapper.writeValueAsString(newsletterSubscriberDto);

        mockMvc.perform(post(newsletterSubscriberControllerLink + "/subscribe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(dtoJson))
                .andExpect(status().isOk());

        verify(newsletterSubscriberService).subscribe(any(NewsletterSubscriberDto.class));
    }
}
