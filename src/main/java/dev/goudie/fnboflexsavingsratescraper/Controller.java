package dev.goudie.fnboflexsavingsratescraper;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {
    private final Handler handler;

    public Controller(Handler handler) {
        this.handler = handler;
    }

    @GetMapping(path = "vapid_public_key", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public byte[] getVapidPublicKey(HttpServletResponse httpServletResponse) {
        httpServletResponse.setHeader(
                "cache-control",
                "s-maxage=1, stale-while-revalidate=599"
        );
        return handler.getVapidPublicKey();
    }
}
