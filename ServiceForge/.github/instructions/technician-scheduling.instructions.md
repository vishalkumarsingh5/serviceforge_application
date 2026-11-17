---
applyTo: "backend/src/main/java/com/serviceforge/service/**/*.java,backend/src/test/java/com/serviceforge/service/**/*.java"
---

# Technician Scheduling Instructions

Apply these instructions when implementing or fixing technician job scheduling, availability, or
booking-conflict behavior.

## Original bug

`TechnicianAvailabilityService.bookJob(...)` originally detected a conflict only when an existing
job had exactly the same start time as the requested job. That point-in-time comparison missed
jobs with different start times whose scheduled windows overlapped, allowing a technician to be
booked for conflicting work.

## Incorrect implementation pattern

Do not check only:

```java
existing.getStartTime().equals(startTime)
```

Equal start times identify only one special case. They do not detect partial overlaps or one
booking contained within another. Also avoid checking just whether the new start falls inside an
existing interval; that misses cases where the new interval contains the existing one.

## Correct interval-overlap validation

Compare the requested interval against every existing job for the same technician. Treat job
windows as half-open intervals `[start, end)`: the start is included and the end is excluded.

For new interval `[newStart, newEnd)` and existing interval
`[existingStart, existingEnd)`, they overlap when:

```text
newStart < existingEnd AND newEnd > existingStart
```

Use strict comparisons (in Java, `isBefore` and `isAfter`). Reject the booking if any same-
technician job meets both conditions, and do so before saving the new job. This allows adjacent
intervals where one job ends exactly as the next begins under overlap-only semantics.

## Travel buffer considerations

Feature 1 records a 45-minute travel-buffer decision, represented by
`TechnicianAvailabilityService.TRAVEL_BUFFER_MINUTES`. Preserve that value and its existing
behavior when fixing overlap detection. Do not silently add 45 minutes to the interval-overlap
predicate or change adjacency behavior as part of an overlap-only fix.

Treat travel-gap enforcement as a separate business rule. If a task explicitly requires it, use the
existing constant rather than a duplicated literal, clarify the exact boundary (for example,
whether exactly 45 minutes is allowed), and test both chronological booking orders. If repository
documentation and implementation disagree about current behavior, call out the discrepancy rather
than silently choosing a new policy.

## Common mistakes

- Comparing start times for equality instead of comparing both interval boundaries.
- Using inclusive comparisons that reject adjacent bookings.
- Checking only one direction of containment.
- Comparing bookings for different technicians and raising false conflicts.
- Checking only one existing booking rather than all of the technician's existing jobs.
- Applying the travel buffer implicitly and changing behavior without an explicit requirement.
- Saving a job before conflict detection is complete.
- Leaving comments or tests that still describe the fixed bug as current behavior.

## Required regression tests

Use deterministic date/time values where practical. Cover at least:

- Partial overlap with different start times (for example, existing 09:00–11:00 and new
  10:00–12:00): reject.
- New interval contained in an existing interval: reject.
- Existing interval contained in the new interval: reject.
- Identical windows or identical start times: reject.
- Adjacent intervals (existing 09:00–11:00 and new 11:00–12:00): allow under overlap-only
  semantics.
- Non-overlapping windows before and after an existing job: allow.
- Same time window for a different technician: allow.
- Conflict rejection leaves the store unchanged and does not create a job.
- If travel-gap enforcement is explicitly in scope: test less than, exactly, and more than 45
  minutes between jobs in both booking orders.

## Validation checklist

- [ ] Read the relevant feature specification, decision log, and current implementation.
- [ ] Check only jobs belonging to the technician being booked.
- [ ] Apply the full interval predicate using both boundaries and strict comparisons.
- [ ] Reject a conflict before creating or saving a job.
- [ ] Preserve the travel-buffer behavior unless a separate, explicit requirement changes it.
- [ ] Add regression coverage for partial overlap, both containment directions, identical windows,
  adjacency, non-overlap, and technician isolation.
- [ ] Verify conflict rejection does not persist the requested job.
- [ ] Update obsolete comments and run the narrowest relevant backend tests.
