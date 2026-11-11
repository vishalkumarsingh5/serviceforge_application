# Rule: Reject overlapping technician bookings

## Business rule

A technician must not be booked for two jobs whose scheduled time intervals overlap. When booking
a job, compare it with every existing job for the same technician. Reject the new booking with a
clear conflict error if the intervals overlap; do not save it.

Treat job intervals as half-open ranges: the start is included and the end is excluded. For a new
job `[newStart, newEnd)` and an existing job `[existingStart, existingEnd)`, they overlap exactly
when:

```text
newStart < existingEnd AND newEnd > existingStart
```

Use strict comparisons. This catches partial overlaps and jobs contained within one another, while
allowing a job to start exactly when another job ends.

## Exact implementation steps

1. Find the technician by ID. If no technician exists, preserve the existing unknown-technician
   error behavior.
2. Retrieve that technician's jobs only. Jobs belonging to other technicians must not block this
   booking.
3. Compare the requested interval with each existing job using the overlap predicate above.
4. If any comparison matches, throw the service's conflict exception before allocating/saving the
   new job. Do not create a booking on conflict.
5. Otherwise, create and save the job as before.
6. Cover the rule with tests for partial overlap, containment, an identical interval, a same-time
   job for another technician, a non-overlapping interval, and adjacent intervals. Keep the
   existing unknown-technician test.

## Examples

Assume the same technician already has a job from 09:00 to 11:00:

| Requested booking | Result | Reason |
|---|---|---|
| 10:00–12:00 | Reject | Partial overlap |
| 08:00–10:00 | Reject | Ends during the existing job |
| 09:30–10:00 | Reject | Fully contained in the existing job |
| 09:00–11:00 | Reject | Identical interval |
| 11:00–12:00 | Allow under the overlap rule | Starts at the existing job's exclusive end |
| 12:00–13:00 | Allow under the overlap rule | No interval overlap |

A booking for a different technician at 10:00–12:00 is not a conflict.

## Travel buffer consideration

The 45-minute travel-buffer policy (`TRAVEL_BUFFER_MINUTES`) is separate from the interval
overlap check. Do not change the constant or silently widen/narrow its behavior as part of fixing
overlap detection. Apply the interval predicate to the scheduled job start and end times. If
enforcing the travel gap is also required, keep that as a distinct check with its own explicit
boundary behavior and tests; for example, an immediately adjacent booking is allowed by the
overlap predicate, but may be disallowed by an independently enforced 45-minute travel policy.

## Reflection

Checking only whether two jobs have identical start times confuses a point comparison with an
interval conflict. Comparing both interval boundaries addresses partial overlaps and containment
without rejecting legitimate adjacency. Tests should pin down those boundary cases so future
changes do not reintroduce the bug or accidentally alter the separate travel-buffer policy.
