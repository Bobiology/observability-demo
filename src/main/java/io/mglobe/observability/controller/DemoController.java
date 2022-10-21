package io.mglobe.observability.controller;

import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.net.http.HttpResponse;
import java.util.Random;

@RestController
@Timed
public class DemoController {
    //Micrometer registry to store all custom metrics
    public final MeterRegistry meterRegistry;

    public DemoController(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }
    //Get API
    @GetMapping("/")
    public String hello(){
        return "Hello World\n";
    }
    //Update API
    @GetMapping("/update")
    public String callAnotherService(){
        //Generate a random number
        Random random = new Random();
        int rvalue = random.nextInt(1000);

        //put number into a custom metric
        Gauge.builder("bootifulWavefront.randomValue",
                rvalue,
                Integer::intValue)
                .description("Latest Random Value")
                .register(meterRegistry);

        //call another service to use the number
        String update = String.format("%d", rvalue);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.postForObject("http://localhost:8089/update", update, String.class);

        return rvalue+ "\n";
    }
}
