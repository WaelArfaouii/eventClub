package org.example.springbootsecurityjwt.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "events")
@Data
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 100)
    private String eventName;

    @NotBlank
    @Size(max = 100)
    private String organizerName;

    @NotBlank
    @Size(max = 500)
    private String eventDescription;

    private LocalDate eventDate;

    @NotBlank
    @Size(max = 200)
    private String eventLocation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToMany(mappedBy = "eventParticipations", fetch = FetchType.LAZY)
    private List<User> participants = new ArrayList<>();

    private boolean userParticipating = false;

    public Event() {
    }

    public Event(String eventName, String eventDescription, LocalDate eventDate,
                 String organizerName,String eventLocation, User user) {
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.eventDate = eventDate;
        this.organizerName = organizerName;
        this.eventLocation = eventLocation;
        this.user = user;
    }
}