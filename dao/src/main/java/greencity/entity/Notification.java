package greencity.entity;

import greencity.enums.NotificationSource;
import greencity.enums.NotificationSourceType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "section", nullable = false)
    private NotificationSource section;

    @Enumerated(EnumType.STRING)
    @Column(name = "source_type", nullable = false)
    private NotificationSourceType sectionType;

    @Column(name = "text", nullable = false)
    private String text;

    @Column(name = "is_read")
    private boolean isRead;

    @Column(name = "received_time")
    private LocalDateTime receivedTime;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
