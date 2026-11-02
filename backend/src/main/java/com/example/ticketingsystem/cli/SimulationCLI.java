package com.example.ticketingsystem.cli;
import java.util.Scanner;
import com.example.ticketingsystem.service.SimulationService;
import java.util.concurrent.atomic.AtomicBoolean;
public class SimulationCLI {
 public static void main(String[] args) {
  SimulationService service=new SimulationService();Scanner input=new Scanner(System.in);
  System.out.println("Real-Time Ticketing CLI (type help)");
  AtomicBoolean monitoring = new AtomicBoolean(true);
  Thread monitor = new Thread(() -> {
   while (monitoring.get()) {
    var s = service.snapshot();
    if (s.running()) System.out.printf("[LIVE] Available: %d | Sold: %d | Released: %d/%d%n",s.available(),s.sold(),s.released(),s.target());
    try {Thread.sleep(2000);} catch (InterruptedException e) {return;}
   }
  }, "cli-monitor");
  monitor.setDaemon(true);monitor.start();
  while(true) {System.out.print("ticketing> ");if(!input.hasNextLine())break;String cmd=input.nextLine().trim().toLowerCase();
   try {switch(cmd) {
    case "help" -> System.out.println("configure | start | stop | reset | status | exit");
    case "configure" -> {int total=ask(input,"Total tickets");int release=ask(input,"Vendor release interval (ms)");int retrieve=ask(input,"Customer purchase interval (ms)");int cap=ask(input,"Max pool capacity");int vendors=ask(input,"Vendor threads");int customers=ask(input,"Customer threads");service.configure(new SimulationService.Settings(total,release,retrieve,cap,vendors,customers));System.out.println("Configuration saved");}
    case "start" -> {service.start();System.out.println("Started");}
    case "stop" -> {service.stop();System.out.println("Stopped");}
    case "reset" -> {service.reset();System.out.println("Reset");}
    case "status" -> {var s=service.snapshot();System.out.printf("Running: %s | Available: %d | Sold: %d | Released: %d/%d%n",s.running(),s.available(),s.sold(),s.released(),s.target());s.logs().stream().limit(12).forEach(System.out::println);}
    case "exit" -> {monitoring.set(false);service.stop();return;}
    default -> System.out.println("Unknown command. Type help.");
   }}catch(Exception e){System.out.println("Error: "+e.getMessage());}
  }
 }
 private static int ask(Scanner sc,String label) {while(true){System.out.print(label+": ");try{int n=Integer.parseInt(sc.nextLine().trim());if(n>0)return n;}catch(NumberFormatException ignored){}System.out.println("Please enter a positive whole number.");}}
}
