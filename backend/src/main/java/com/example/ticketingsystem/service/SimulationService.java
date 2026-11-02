package com.example.ticketingsystem.service;

import org.springframework.stereotype.Service;
import com.example.ticketingsystem.entity.TicketPool;
import com.example.ticketingsystem.worker.VendorWorker;
import com.example.ticketingsystem.worker.CustomerWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalTime;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/** Coordinates producer and consumer workers. All lifecycle transitions use one monitor. */
@Service
public class SimulationService {
 public record Settings(int totalTickets, int ticketReleaseRate, int customerRetrievalRate,
                        int maxTicketCapacity, int vendors, int customers) {}
 public record Snapshot(boolean running, int available, int sold, int released,
                        int target, int capacity, List<String> logs, Settings settings) {}
 private static final Logger LOGGER = LoggerFactory.getLogger(SimulationService.class);
 private final Object monitor = new Object();
 private final Deque<String> logs = new ArrayDeque<>();
 private TicketPool pool = new TicketPool(100);
 private Settings settings = new Settings(100, 500, 800, 100, 2, 3);
 private boolean running;
 private int released;
 private ExecutorService executor;
 private long generation;

 private void log(String message) {
  LOGGER.info(message);
  logs.addFirst(LocalTime.now().withNano(0) + "  " + message);
  while (logs.size() > 120) logs.removeLast();
 }
 private static void validate(Settings s) {
  if (s == null || s.totalTickets() <= 0 || s.ticketReleaseRate() <= 0 ||
      s.customerRetrievalRate() <= 0 || s.maxTicketCapacity() <= 0 ||
      s.vendors() <= 0 || s.customers() <= 0 || s.vendors() > 50 || s.customers() > 50)
   throw new IllegalArgumentException("Ticket counts, intervals and capacity must be positive; workers must be between 1 and 50");
 }
 public Snapshot configure(Settings s) {
  validate(s);
  synchronized(monitor) {
   if(running) throw new IllegalStateException("Stop the simulation before configuring");
   terminateWorkers();
   settings=s; pool=new TicketPool(s.maxTicketCapacity()); released=0; logs.clear();
   log("Configured simulation"); return snapshotLocked();
  }
 }
 public Snapshot start() {
  synchronized(monitor) {
   if(running) return snapshotLocked();
   if(released>=settings.totalTickets()) throw new IllegalStateException("Reset to run again");
   terminateWorkers();
   running=true;
   long run=++generation;
   executor=Executors.newFixedThreadPool(settings.vendors()+settings.customers());
   for(int i=1;i<=settings.vendors();i++) executor.submit(new VendorWorker(this,i,run));
   for(int i=1;i<=settings.customers();i++) executor.submit(new CustomerWorker(this,i,run));
   log("Simulation started"); return snapshotLocked();
  }
 }
 /** Producer action is atomic with the total-release limit. */
 public boolean release(int id,long run) {
  synchronized(monitor) {
   if(!running || generation!=run || released>=settings.totalTickets()) return false;
   if(!pool.addTicket()) return false;
   released++; log("Vendor "+id+" released ticket #"+released); return true;
  }
 }
 public boolean purchase(int id,long run) {
  synchronized(monitor) {
   if(!running || generation!=run) return false;
   boolean bought=pool.removeTicket();
   if(bought) log("Customer "+id+" purchased a ticket");
   if(released>=settings.totalTickets() && pool.available()==0) {
    running=false; generation++; log("All tickets sold");
    if(executor!=null) { executor.shutdownNow(); executor=null; }
   }
   return bought;
  }
 }
 public boolean isActive(long run) { synchronized(monitor) { return running && generation==run; } }
 public boolean allReleased(long run) { synchronized(monitor) { return generation!=run || released>=settings.totalTickets(); } }
 public int releaseInterval() { synchronized(monitor) {return settings.ticketReleaseRate();} }
 public int purchaseInterval() { synchronized(monitor) {return settings.customerRetrievalRate();} }
 private void terminateWorkers() {
  generation++;
  if(executor!=null) { executor.shutdownNow(); executor=null; }
 }
 public Snapshot stop() {
  synchronized(monitor) { running=false; terminateWorkers(); log("Simulation stopped"); return snapshotLocked(); }
 }
 public Snapshot reset() {
  synchronized(monitor) {
   running=false; terminateWorkers(); pool=new TicketPool(settings.maxTicketCapacity());
   released=0; logs.clear(); log("Simulation reset"); return snapshotLocked();
  }
 }
 public Snapshot snapshot() { synchronized(monitor) {return snapshotLocked();} }
 private Snapshot snapshotLocked() {
  return new Snapshot(running,pool.available(),pool.sold(),released,settings.totalTickets(),
    pool.capacity(),List.copyOf(logs),settings);
 }
}
