# Feature 2 Agent Blueprint — Parts Reservation

| Agent | ServiceForge role | Input | Output | Handoff |
|---|---|---|---|---|
| Requirements Agent | BA Agent | Feature 2 intent and existing feature decisions | Scoped, testable feature specification | Send the spec to the Design Agent. |
| Design Agent | Architect Agent | Specification, requirements, existing architecture | Short ADR with key decisions and risks | Send ADR and requirements to the Review Agent. |
| Review Agent | Review Agent | Requirements and ADR | Design review with findings and readiness | If ready, send reviewed inputs to the Planning Agent; otherwise return blockers for resolution. |
| Planning Agent | Planning Agent | Approved requirements, ADR, and design review | Dependency-ordered implementation plan | Send plan and approved scope to the Build Agent. |
| Build Agent | Developer Agent | Approved spec, decisions, rules, and plan | Working implementation and updated artifact list | Send implementation to the Test Agent. |
| Test Agent | Tester Agent | Specification and implementation | Tests and pass/fail report against the Definition of Done | If tests fail, send findings to the Debug Agent; otherwise report completion to the Orchestrator. |
| Debug Agent | Debug Agent | Failed tests or defect report and relevant code | Reproduced root cause and minimal fix recommendation | Hand the fix to the Build Agent, then return to the Test Agent for retesting. |
| Orchestrator Feature 2 Agent | Orchestrator Feature 2 Agent | Feature 2 artifacts and each agent's output | Workflow status, handoffs, blockers, and final test status | Assign agents in order; complete only after the Test Agent passes the Definition of Done. |

## Workflow

**Requirements → Design → Review → Planning → Build → Test → Debug if needed → Build fix → Test again**

The Orchestrator manages the handoffs and blockers. Do not start a step until its required input is
ready. Keep Feature 2's all-or-nothing reservation rule and existing business decisions intact.
