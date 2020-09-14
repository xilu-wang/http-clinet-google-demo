import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.util.ExponentialBackOff;

import java.io.IOException;

public class BackOffRetryDemo {


    public static void main(String[] args) throws IOException {

        ExponentialBackOff backoff = new ExponentialBackOff.Builder()
                .setInitialIntervalMillis(500)
                .setMaxElapsedTimeMillis(900000)
                .setMaxIntervalMillis(6000)
                .setMultiplier(1.5)
                .setRandomizationFactor(0.5)
                .build();

        HttpRequestFactory requestFactory
                = new NetHttpTransport().createRequestFactory();
        HttpRequest request = requestFactory.buildGetRequest(
                new GenericUrl("https://localhost:8080"));


        final HttpUnsuccessfulResponseHandler backoffHandler =
                new HttpBackOffUnsuccessfulResponseHandler(backoff);
        request.setUnsuccessfulResponseHandler(
                (request1, response, supportsRetry) -> {
                    if (backoffHandler.handleResponse(request1, response, supportsRetry)) {
                        // Otherwise, we defer to the judgement of our internal backoff handler.
                        System.out.println("Retrying!!!!!!!!!!!!!!!!!!!!!!!!!");
                        return true;
                    } else {
                        return false;
                    }
                });

        String rawResponse = request.execute().parseAsString();
        System.out.println(rawResponse);
    }
}
