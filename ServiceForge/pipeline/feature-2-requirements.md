# Requirements: Check Stock and Reserve Parts for a Scheduled Job

## Purpose

Allow users to check available part stock and reserve requested quantities for a job that has
already been booked for a technician. A reservation must be all-or-nothing so failed requests
cannot leave stock partially deducted.

## User Stories

- As a dispatcher, I want to view the available quantity of each part so that I can determine
  whether a scheduled job can be supplied.
- As a dispatcher, I want to reserve one or more parts for a scheduled job so that those parts
  are set aside and unavailable to later reservations.
- As a dispatcher, I want an insufficient-stock request to fail without reserving any of its
  parts so that inventory remains consistent.
- As a dispatcher, I want a clear rejection when a job or part is invalid so that I can correct
  the request.

## Acceptance Criteria

1. The system provides a way to retrieve the parts catalog and each part's currently available
   quantity.
2. The system accepts a request containing an existing job and one or more part-and-quantity
   entries.
3. A request is eligible only when its job exists, has a technician assigned through a booked
   technician slot, and is in the scheduled state. A job without a booked technician slot must
   not receive a reservation.
4. Every requested part must exist, and every requested quantity must be a positive whole number.
5. If all requested parts have sufficient available stock, the system records the complete
   reservation against the job and decreases each part's available stock by the reserved total.
6. If any requested part is invalid or has insufficient stock, the system rejects the request,
   records no reservation, and leaves the available quantity of every part unchanged.
7. If a part appears more than once in a request, the quantities are summed before availability
   is checked and the reservation is recorded.
8. Stock reserved for a job is not available to fulfill a later reservation.
9. Implementing this feature does not change Feature 1's 45-minute travel-buffer policy.

## Edge Cases

- Unknown job ID.
- Existing job with no booked technician slot or no technician assignment.
- Job that exists but is not scheduled.
- Unknown part ID.
- Quantity of zero, a negative quantity, a fractional quantity, or a missing quantity.
- Empty part list or an empty request.
- Duplicate entries for a part whose combined quantity is sufficient, or exceeds available stock.
- One part in a multi-part request is unavailable while other requested parts are available; the
  entire request must fail with no partial reservation or stock change.
- A request exactly equal to a part's available quantity succeeds and leaves zero available.
- A subsequent request for stock already reserved by another job is rejected when it exceeds the
  remaining available quantity.
- A reservation failure must not create a reservation record or consume a job/part association.

## Dependencies

- **Feature 1 — Technician Availability Calendar:** reservations target an existing scheduled job
  whose technician slot has already been booked; this feature does not create or assign technician
  slots.
- **Feature 1 decision — standard travel buffer per job is 45 minutes:** preserve this policy; part
  stock reservation does not alter job scheduling or travel-buffer behavior.
- **Feature 2 specification — Check Stock and Reserve Parts for a Scheduled Job:**
  `features/feature-2-stock-and-parts-reservation.md`, which defines the mock/in-memory
  stock approach, all-or-nothing reservation, and explicit exclusions.

## Metrics

- **Oversell incidents:** count of successful reservations that cause available stock to fall
  below zero. Target: zero.
- **Atomicity failures:** count of rejected requests that nevertheless create a reservation or
  change any part's available quantity. Target: zero.
- **Reservation success rate:** successful reservation requests divided by all reservation
  requests, reported over a chosen reporting period.
- **Rejection rate by reason:** rejected requests grouped by invalid job/status/technician slot,
  unknown part, invalid quantity, and insufficient stock.
- **Reservation processing latency:** elapsed time from request acceptance to success or
  rejection, reported as a distribution (for example, median and 95th percentile).
