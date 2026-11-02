package com.example.ticketingsystem;
import com.example.ticketingsystem.service.SimulationService;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class SimulationServiceTest {
 @Test void invalidSettingsRejected() {
  SimulationService service=new SimulationService();
  assertThrows(IllegalArgumentException.class,()->service.configure(new SimulationService.Settings(10,0,1,2,1,1)));
  assertThrows(IllegalArgumentException.class,()->service.configure(new SimulationService.Settings(10,1,1,2,51,1)));
 }
 @Test void resetClearsCounters() {
  SimulationService service=new SimulationService();
  service.configure(new SimulationService.Settings(10,100,100,3,1,1));
  service.start();service.stop();
  var result=service.reset();
  assertFalse(result.running());assertEquals(0,result.released());assertEquals(0,result.sold());
  assertEquals(0,result.available());
 }
 @Test void concurrentRunNeverExceedsTotal() throws Exception {
  SimulationService service=new SimulationService();
  service.configure(new SimulationService.Settings(20,1,1,3,4,4));
  service.start();
  long deadline=System.currentTimeMillis()+5000;
  while(service.snapshot().running() && System.currentTimeMillis()<deadline) Thread.sleep(10);
  var result=service.snapshot(); service.stop();
  assertFalse(result.running(),"Simulation should complete within five seconds");
  assertEquals(20,result.released());assertEquals(20,result.sold());
  assertEquals(0,result.available());
 }
}
