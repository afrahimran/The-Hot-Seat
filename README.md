# The Hot Seat

Real-time event ticketing simulation demonstrating Java OOP, Runnable producers and consumers, synchronization, and a React dashboard.

## Java package layout
- `entity/TicketPool.java` — bounded synchronized ticket queue
- `worker/VendorWorker.java` and `CustomerWorker.java` — Runnable producer/consumer
- `service/SimulationService.java` — configuration, lifecycle, counters and log
- `controller/SimulationController.java` — REST API
- `springboot/TicketingSystemApplication.java` — Spring Boot entry point
- `cli/SimulationCLI.java` — console entry point

Logs are written to `backend/logs/the-hot-seat.log` and a recent activity window is shown on the dashboard. 