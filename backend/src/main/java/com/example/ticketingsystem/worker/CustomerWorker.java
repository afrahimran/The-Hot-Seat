package com.example.ticketingsystem.worker;
import com.example.ticketingsystem.service.SimulationService;
/** A consumer that periodically attempts to purchase one ticket. */
public final class CustomerWorker implements Runnable {
 private final SimulationService service;
 private final int id;
 private final long generation;
 public CustomerWorker(SimulationService service,int id,long generation) {
  this.service=service;this.id=id;this.generation=generation;
 }
 @Override public void run() {
  try {
   while(service.isActive(generation)) {
    service.purchase(id,generation);
    Thread.sleep(service.purchaseInterval());
   }
  } catch(InterruptedException e) {Thread.currentThread().interrupt();}
 }
}
