# Testing and verification

Run `cd backend && mvn clean test` and `cd frontend && npm install && npm run build`. Tests included: capacity and empty pool, 200 simultaneous consumers with 50 tickets, invalid configuration, reset, and completion under concurrent workers.

## Manual checks to perform and record

1. Save a valid configuration and start; available never exceeds capacity.
2. Set a zero or negative field; the API returns HTTP 400.
3. Stop while workers are active; counters stop changing.
4. Reset; available, sold and released return to zero.
5. Run to completion; released = sold = target and available = 0.
6. Refresh browser and verify status reloads.
7. Inspect `backend/logs/the-hot-seat.log` for activity.

**Evidence status:** Tests are included but their results have not been verified in this packaging environment. Do not mark manual checks as passed until actually performed.
