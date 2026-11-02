package com.example.ticketingsystem.worker;
import com.example.ticketingsystem.service.SimulationService;
/** A producer that periodically releases tickets into the shared pool. */
public final class VendorWorker implements Runnable {
 private final SimulationService service;
 private final int id;
 private final long generation;
 public VendorWorker(SimulationService service,int id,long generation) {
  this.service=service;this.id=id;this.generation=generation;
 }
 @Override public void run() {
  try {
   while(service.isActive(generation)) {
    if(!service.release(id,generation) && service.allReleased(generation)) return;
    Thread.sleep(service.releaseInterval());
   }
  } catch(InterruptedException e) {Thread.currentThread().interrupt();}
 }
}
