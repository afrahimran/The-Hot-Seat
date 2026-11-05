package com.example.ticketingsystem.controller;
import org.springframework.web.bind.annotation.*;
import com.example.ticketingsystem.service.SimulationService;
import org.springframework.http.HttpStatus;

@RestController @RequestMapping("/api") @CrossOrigin(origins={"http://localhost:5173", "https://ths-tau-ashen.vercel.app"})
public class SimulationController {
 private final SimulationService service;
 public SimulationController(SimulationService service) {this.service=service;}
 @GetMapping("/status") public SimulationService.Snapshot status(){return service.snapshot();}
 @PostMapping("/configure") public SimulationService.Snapshot configure(@RequestBody SimulationService.Settings s){return service.configure(s);}
 @PostMapping("/start") public SimulationService.Snapshot start(){return service.start();}
 @PostMapping("/stop") public SimulationService.Snapshot stop(){return service.stop();}
 @PostMapping("/reset") public SimulationService.Snapshot reset(){return service.reset();}
 @ExceptionHandler({IllegalArgumentException.class,IllegalStateException.class}) @ResponseStatus(HttpStatus.BAD_REQUEST)
 public java.util.Map<String,String> handle(RuntimeException e){return java.util.Map.of("error",e.getMessage());}
}
