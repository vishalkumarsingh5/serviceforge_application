# Feature 2 Design Review

## Scenario Review

- **Competing requests for last part:** one succeeds; stock never becomes negative.
- **No stock:** no reservation; show an unavailable message.
- **Duplicate submission:** the idempotency key ensures only one reservation.
- **Release twice:** restore stock only once; release is outside Feature 2 scope.
- **Bypass attempt or critic rejects/is unavailable:** notes cannot override rules; do not finalize
  without a successful critic check.

## Finding 1 — Atomicity and rule enforcement

- Synchronize validation, stock check, reservation creation, and stock decrement to prevent
  overselling and partial updates.
- Enforce business rules in the backend, not only in the UI.

## Finding 2 — Critic and release scope

- Requirements and ADR do not define a critic. If used, rejection or unavailability must fail
  closed before any stock mutation.
- Release is out of scope; future release must restore stock atomically and only once.

## Improvement

- Define how callers supply idempotency keys; test concurrent last-unit requests, no-stock
  atomicity, duplicate submission, and critic failure if applicable.

## Readiness

**Needs changes** — clarify whether a critic is in scope and define the idempotency-key contract.
