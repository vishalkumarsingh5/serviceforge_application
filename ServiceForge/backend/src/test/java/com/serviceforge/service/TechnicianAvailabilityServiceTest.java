package com.serviceforge.service;

import com.serviceforge.data.MockDataStore;
import com.serviceforge.model.Job;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TechnicianAvailabilityServiceTest {

    private MockDataStore dataStore;
    private TechnicianAvailabilityService service;

    @BeforeEach
    void setUp() {
        dataStore = new MockDataStore();
        dataStore.seed();
        service = new TechnicianAvailabilityService(dataStore);
    }

    @Test
    void booksAJobForAnAvailableTechnician() {
        LocalDateTime start = LocalDateTime.now().withHour(16).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime end = start.plusHours(1);

        Job job = service.bookJob(1L, "New Customer", start, end);

        assertEquals("New Customer", job.getCustomerName());
        assertEquals(1L, job.getTechnicianId());
    }

    @Test
    void rejectsABookingAtAnIdenticalStartTimeAsAnExistingJob() {
        LocalDateTime start = LocalDateTime.now().withHour(9).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime end = start.plusHours(1);
        // Technician 1 already has a seeded job starting at 09:00 (see MockDataStore.seed()).

        assertThrows(IllegalStateException.class, () -> service.bookJob(1L, "Conflicting Customer", start, end));
    }

    @Test
    void rejectsABookingThatOverlapsAnExistingJobWithADifferentStartTime() {
        LocalDateTime start = LocalDateTime.now().withHour(10).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime end = start.plusHours(1);
        // Technician 1 has a seeded job from 09:00 to 11:00.

        assertThrows(IllegalStateException.class, () -> service.bookJob(1L, "Conflicting Customer", start, end));
    }

    @Test
    void allowsABookingThatStartsWhenAnExistingJobEnds() {
        LocalDateTime start = LocalDateTime.now().withHour(11).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime end = start.plusHours(1);

        Job job = service.bookJob(1L, "Next Customer", start, end);

        assertEquals("Next Customer", job.getCustomerName());
    }

    @Test
    void throwsForAnUnknownTechnician() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = start.plusHours(1);

        assertThrows(IllegalArgumentException.class, () -> service.bookJob(999L, "Nobody", start, end));
    }
}
