package greencity.entity;

import greencity.enums.EventRole;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode(exclude = "event")
@Table(name = "event_participants")
public class EventParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @JoinColumn(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_role", nullable = false)
    private EventRole eventRole;

    // Static factory method to create a new participant
    public static EventParticipant createParticipant(Event event, Long userId, EventRole role) {
        EventParticipant participant = new EventParticipant();
        participant.setEvent(event);
        participant.setUserId(userId);
        participant.setEventRole(role);
        participant.setJoinedAt(LocalDateTime.now());
        return participant;
    }
}
