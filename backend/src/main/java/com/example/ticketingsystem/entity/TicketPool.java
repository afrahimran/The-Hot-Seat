package com.example.ticketingsystem.entity;
import java.util.ArrayDeque;
import java.util.Queue;
/** One shared monitor protects both capacity and ticket sales. */
public class TicketPool {
 private final Queue<Integer> tickets = new ArrayDeque<>();
 private final int capacity;
 private int nextId = 1;
 private int sold = 0;
 public TicketPool(int capacity) { if(capacity <= 0) throw new IllegalArgumentException("Capacity must be positive"); this.capacity=capacity; }
 public synchronized boolean addTicket() {
   if(tickets.size() >= capacity) return false;
   tickets.add(nextId++); notifyAll(); return true;
 }
 public synchronized boolean removeTicket() {
   if(tickets.isEmpty()) return false;
   tickets.remove(); sold++; notifyAll(); return true;
 }
 public synchronized int available() { return tickets.size(); }
 public synchronized int sold() { return sold; }
 public int capacity() { return capacity; }
}
