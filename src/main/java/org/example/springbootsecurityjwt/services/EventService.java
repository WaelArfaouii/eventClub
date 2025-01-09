package org.example.springbootsecurityjwt.services;

import org.example.springbootsecurityjwt.models.ERole;
import org.example.springbootsecurityjwt.models.Event;
import org.example.springbootsecurityjwt.models.User;
import org.example.springbootsecurityjwt.repositories.EventRepository;
import org.example.springbootsecurityjwt.repositories.UserRepository;
import org.example.springbootsecurityjwt.request.EventParticipationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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
        // Get the currently authenticated user
        User user = userRepository.getUserByEmail(this.getConnectedUser());

        // Fetch the event
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        // Log if the user is already a participant in the event
        System.out.println(event.getParticipants().contains(user));

        // Check if the user is already a participant in the event
        if (!event.getParticipants().contains(user)) {
            // Add user to the event's participants list
            event.getParticipants().add(user);

            // Add event to the user's event participation list
            user.getEventParticipations().add(event);

            // Persist changes to the event and user in the database
            eventRepository.save(event);    // Save the event
            userRepository.save(user);      // Save the user to persist changes
        } else {
            throw new RuntimeException("You are already participating in this event.");
        }
    }



    public String getConnectedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                return ((UserDetails) principal).getUsername();
            } else {
                return principal.toString(); // For cases like OAuth where the principal might be a string
            }
        }
        return null;
    }

    private User getCurrentUser() {
        String username = this.getConnectedUser(); // This retrieves the authenticated user's username
        return userRepository.findByEmail(username).orElseThrow(() -> new RuntimeException("User not found"));
    }

    /**
     * Get the participation status for each event
     */
    public List<EventParticipationDTO> getParticipations() {
        // Get the currently authenticated user
        User currentUser = getCurrentUser();

        // Get all events from the repository
        List<Event> events = eventRepository.findAll();

        // Convert events into DTO with participation status
        return events.stream().map(event -> {
            boolean isParticipated = event.getParticipants().contains(currentUser); // Check if the user is a participant
            return new EventParticipationDTO(
                    event.getId(),
                    event.getEventName(),
                    event.getEventDescription(),
                    event.getEventDate().toString(),
                    event.getEventLocation(),
                    isParticipated
            );
        }).collect(Collectors.toList());
    }



}