package com.example.ticketingsystem;
import com.example.ticketingsystem.entity.TicketPool;
import org.junit.jupiter.api.Test;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.*;
class TicketPoolTest {
 @Test void capacityAndEmptyPool() {TicketPool p=new TicketPool(2);assertFalse(p.removeTicket());assertTrue(p.addTicket());assertTrue(p.addTicket());assertFalse(p.addTicket());assertTrue(p.removeTicket());assertEquals(1,p.available());assertEquals(1,p.sold());}
 @Test void concurrentConsumersNeverOversell() throws Exception {TicketPool p=new TicketPool(50);for(int i=0;i<50;i++)p.addTicket();ExecutorService threads=Executors.newFixedThreadPool(20);CountDownLatch start=new CountDownLatch(1);java.util.List<Future<Boolean>> futures=new java.util.ArrayList<>();for(int i=0;i<200;i++)futures.add(threads.submit(()->{start.await();return p.removeTicket();}));start.countDown();int success=0;for(Future<Boolean> f:futures)if(f.get())success++;threads.shutdownNow();assertEquals(50,success);assertEquals(0,p.available());assertEquals(50,p.sold());}
}
