package com.ehsan.pdf.pdfspring;

import com.lowagie.text.DocumentException;
import org.apache.commons.io.IOUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.xhtmlrenderer.pdf.ITextRenderer;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Duration;
import java.time.LocalTime;

@RestController
public class PdfController {

    @GetMapping(
            value = "/get-pdf"
            //produces = MediaType.APPLICATION_PDF_VALUE
    )
    public ResponseEntity<byte[]> getImageWithMediaType() throws IOException, DocumentException {

        //Flying Saucer part
        ITextRenderer renderer = new ITextRenderer();
        String html = "<html><head><style>.text-color{color:red;}</style></head><body><h6 class='text-color'>Ehsan PDF</h6><div><img width='10' height='5' src=\"https://media.wired.com/photos/598e35994ab8482c0d6946e0/master/w_2560%2Cc_limit/phonepicutres-TA.jpg\"/></div></body></html>";
        renderer.setDocumentFromString(html);
        ByteArrayOutputStream fos = new ByteArrayOutputStream(html.length());
        renderer.layout();
        renderer.createPDF(fos);
        return ResponseEntity
                .ok()
                .contentType(
                        MediaType.parseMediaType("application/pdf"))
                .body(fos.toByteArray());
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
