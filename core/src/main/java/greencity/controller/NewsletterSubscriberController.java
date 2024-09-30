package greencity.controller;

import greencity.constant.HttpStatuses;
import greencity.dto.newslettersubscriber.NewsletterSubscriberDto;
import greencity.service.NewsletterSubscriberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/newsletter-subscribers")
public class NewsletterSubscriberController {
    private final NewsletterSubscriberService newsletterSubscriberService;

    @Operation(summary = "Subscribe to the newsletter")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST)
    })
    @PostMapping("/subscribe")
    public ResponseEntity<NewsletterSubscriberDto> subscribe(@Valid @RequestBody NewsletterSubscriberDto newsletterSubscriberDto) {
        return ResponseEntity.ok(newsletterSubscriberService.subscribe(newsletterSubscriberDto));
    }
}
