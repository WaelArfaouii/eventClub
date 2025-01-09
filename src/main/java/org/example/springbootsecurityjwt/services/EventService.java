package org.example.springbootsecurityjwt.services;

import org.example.springbootsecurityjwt.models.ERole;
import org.example.springbootsecurityjwt.models.Event;
import org.example.springbootsecurityjwt.models.User;
import org.example.springbootsecurityjwt.repositories.EventRepository;
import org.example.springbootsecurityjwt.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    public Event createEvent(String eventName, String eventDescription,
                             LocalDate eventDate, String eventLocation, String organizerName) {
        // Get the currently authenticated user
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetails.getUsername();

        // Find the user by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.getRoles().forEach(role -> {
            System.out.println("Role: " + role.getName());
        });

        // Check if the user has the ADMIN role
        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getName() == ERole.ROLE_ADMIN);

        if (!isAdmin) {
            throw new RuntimeException("Only users with the CLUB role can create events");
        }

        // Create and save the event
        Event event = new Event(eventName, eventDescription, eventDate,organizerName, eventLocation, user);
        return eventRepository.save(event);
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public void deleteEventById(Long id) {
        eventRepository.deleteById(id);
    }

    public Event getEventById(Long id) {
        return eventRepository.findById(id).orElseThrow(() -> new RuntimeException("Event not found"));
    }

    public void updateEvent(Event event) {
        System.out.println("event id : " + event.getId());
        Event existingEvent = getEventById(event.getId());
        existingEvent.setEventName(event.getEventName());
        existingEvent.setEventDescription(event.getEventDescription());
        existingEvent.setEventDate(event.getEventDate());
        existingEvent.setEventLocation(event.getEventLocation());

        Event savedEvent = eventRepository.save(existingEvent);
        System.out.println("Event id : " + savedEvent.getId());

    }

    public void addParticipant(Long eventId) {
        User user = this.getConnectedUser() ;
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        // Check if the user is already a participant
        if (!event.getParticipants().contains(user)) {
            event.getParticipants().add(user);
            eventRepository.save(event);
        } else {
            throw new RuntimeException("You are already participating in this event.");
        }
    }

    public User getConnectedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User users = (User) authentication.getPrincipal();
        return this.userRepository.findById(users.getId()).orElse(null);
    }

}