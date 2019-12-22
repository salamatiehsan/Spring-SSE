package com.ehsan.pdf.pdfspring;

import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.LocalTime;

@RestController
public class PdfController {

    @GetMapping(value = "/helloworld")
    public String helloWorld() {
        return "Hello world";
    }
    @GetMapping(value = "/fluxsteam", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public ConnectableFlux<Object> pdf() {
        return Flux.create(fluxSink -> {
            while (true) {
                fluxSink.next(String.valueOf(System.currentTimeMillis()));
            }
        })
                .sample(Duration.ofSeconds(2)).publish();
    }

    @GetMapping("/stream-sse")
    public Flux<ServerSentEvent<String>> streamEvents() {
        return Flux.interval(Duration.ofSeconds(1))
                .onBackpressureBuffer(3)
                .map(sequence -> ServerSentEvent.<String>builder()
                        .id(String.valueOf(sequence))
                        .event("periodic-event")
                        .data("SSE - " + LocalTime.now().toString())
                        .build());
    }
}
