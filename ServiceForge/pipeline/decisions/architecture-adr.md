# Architecture ADR: Feature 2 Parts Reservation

## Context

Feature 2 reserves parts for scheduled jobs using the in-memory `MockDataStore`. Reservations must
not partially update stock or be applied twice for duplicate requests.

## Decision

Perform job/part validation, quantity aggregation, stock checks, reservation creation, and stock
decrement as one synchronized operation. Validate all items before mutation. Use a unique request
key so retries return the existing reservation instead of reserving twice. Release is out of scope;
future releases must restore stock and mark the reservation released atomically.

## Reason

One protected check-and-update prevents overselling and partial failures; idempotency prevents
duplicate reservations while retaining the existing architecture.

## Impact

Reservations are all-or-nothing and tied to scheduled jobs; duplicate requests cannot decrement
stock twice. In-memory stock resets on restart. Feature 1's 45-minute travel-buffer policy remains
unchanged.
