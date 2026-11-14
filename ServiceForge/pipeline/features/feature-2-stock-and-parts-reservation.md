# Feature 2 — Check Stock and Reserve Parts for a Scheduled Job

| Field | Value |
|---|---|
| **Status** | Candidate-built / in progress |
| **One-line intent** | Check available part stock and reserve requested quantities for an existing scheduled job. |
| **Depends on** | Feature 1 decision: **standard travel buffer per job is 45 minutes**; use Feature 1's scheduled-job records as the reservation target without changing the travel-buffer policy. |
| **In scope** | Maintain a parts catalog with available quantities using the application's existing mock/in-memory data approach; show available stock; accept one or more part-and-quantity requests for an existing scheduled job; require positive whole-number quantities and a scheduled job; check availability for all requested parts and reserve the complete request atomically only when every part is available; associate the reservation with the job and reduce the available quantity by the reserved amount. Assume a failed request creates no reservation and changes no stock, and repeated lines for the same part are summed before availability is checked. |
| **Out of scope** | Consuming reserved parts when work is performed; releasing reservations or restoring stock when a job is canceled; low-stock alerts, purchasing, supplier workflows, or stock replenishment; persistent database storage or inventory beyond the mock/in-memory catalog. |
| **Definition of Done** | A caller can retrieve available quantities for catalog parts; a caller can reserve positive quantities of one or more parts for an existing scheduled job; a successful reservation is associated with that job and decreases each part's available quantity by the reserved total; a missing job, a job that is not scheduled, an unknown part, or a non-positive/non-whole quantity is rejected without changing stock; if any requested part has insufficient stock, the entire request is rejected, no reservation is recorded, and no part quantity changes; multiple lines for the same part are aggregated when checking stock and recording the reservation; stock reserved for one job is unavailable to later reservations; the 45-minute travel-buffer policy remains unchanged. |

## Artifacts this feature touches
Filled in later by the developer and tester agents.
