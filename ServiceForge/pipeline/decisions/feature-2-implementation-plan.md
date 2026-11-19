# Feature 2 Implementation Plan — Parts Reservation

| Task ID | Task | Owner | Depends On | Output | Estimate | Done Check |
|---|---|---|---|---|---|---|
| T1 | Define part and reservation data; seed catalog and track available quantities in `MockDataStore`. | Backend Developer | None | In-memory stock and reservation models/store operations | 1–2 days | Catalog lookup returns seeded parts and quantities; reservation records link a job to aggregated part quantities. |
| T2 | Add backend stock lookup and reservation API/service actions for an existing scheduled job. | Backend Developer | T1 | Stock and reservation endpoints with clear success/error responses | 1–2 days | A valid request reserves parts for a scheduled job; unknown or unscheduled jobs are rejected. |
| T3 | Add reservation controls: validate all inputs and stock before mutation; synchronize check-and-reserve; enforce idempotency keys. | Backend Developer | T1, T2 | Protected all-or-nothing reservation operation | 2–3 days | Two valid requests competing for the final part result in only one reservation; stock never becomes negative. A repeated request with the same key creates no second reservation. |
| T4 | Add UI to view available stock, submit part quantities for a scheduled job, and show success or unavailable/error messages. | Frontend Developer | T2, T3 | Parts stock and reservation workflow in Angular | 2–3 days | User can view stock and submit a reservation; insufficient stock displays an unavailable message and shows no success. |
| T5 | Verify feature behavior, boundaries, and regressions against the requirements. | Tester | T1, T2, T3, T4 | Automated tests and pass/fail report | 1–2 days | Tests verify only one reservation for the final available part; no reservation when stock is unavailable; duplicate requests create only one reservation; critic rejection leaves stock unchanged; and stock never becomes negative. |

## Highest-risk task

- **T3: Reservation controls.** Race conditions may oversell stock or cause partial updates.

## Blocker before implementation

- Confirm whether critic validation is in scope.
- Define the idempotency-key request and response behavior.
- If used, critic rejection or unavailability must leave stock unchanged.
- Reservation release is out of scope; future releases must restore stock only once.
- Keep Feature 1's 45-minute travel buffer unchanged.
