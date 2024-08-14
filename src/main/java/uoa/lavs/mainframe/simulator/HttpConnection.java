package uoa.lavs.mainframe.simulator;

import okhttp3.*;
import uoa.lavs.mainframe.Connection;
import uoa.lavs.mainframe.MessageErrorStatus;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public class HttpConnection implements Connection {
    public static final MediaType MEDIA_TYPE_DATA = MediaType.parse("text/plain; charset=utf-8");

    private final OkHttpClient client;
    private final String baseUrl;
    private long transactionId = 0;

    public HttpConnection(String baseUrl) {
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
        client = initializeClient();
    }

    private OkHttpClient initializeClient() {
        // initialize a new HTTP client
        // WARNING: this code is DANGEROUS - we are ignoring all SSL validation errors
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                    }

                    @Override
                    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                    }

                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new java.security.cert.X509Certificate[]{};
                    }
                }
        };

        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (KeyManagementException e) {
            throw new RuntimeException(e);
        }
        OkHttpClient.Builder newBuilder = new OkHttpClient.Builder();
        newBuilder.sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustAllCerts[0]);
        newBuilder.hostnameVerifier((hostname, session) -> true);
        return newBuilder.build();
    }

    @Override
    public uoa.lavs.mainframe.Response send(uoa.lavs.mainframe.Request request) {
        String postBody = DataParser.convertToData(request, false);
        Request httpRequest = new Request.Builder()
                .url(baseUrl + "api/v1/request")
                .post(RequestBody.create(postBody, MEDIA_TYPE_DATA))
                .build();

        try (Response httpResponse = client.newCall(httpRequest).execute()) {
            if (!httpResponse.isSuccessful()) throw new IOException("Unexpected code " + httpResponse);

            String data = httpResponse.body().string();
            uoa.lavs.mainframe.Response response = DataParser.convertResponseFromData(data, transactionId++);
            return response;
        } catch (IOException ex) {
            return MessageErrorStatus.NETWORK_FAILURE_UNAVAILABLE.generateEmptyResponse(transactionId++);
        }
    }

    @Override
    public void close() throws IOException {
        client.dispatcher().executorService().shutdown();
    }
}
