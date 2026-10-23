package dev.polymarket.client.api;

import dev.polymarket.client.exception.ApiException;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import okhttp3.*;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Abstract base class for API clients with HTTP request handling.
 * <p>
 * Provides common functionality for making HTTP requests using OkHttp,
 * JSON serialization/deserialization with Jackson, and CompletableFuture-based async execution.
 */
public abstract class AbstractApi {

    public static final String EMPTY_PATH = "";
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private static final Map<String, String> OVERLOAD_HEADERS_BY_NAME;

    static {
        Map<String, String> headers = new HashMap<>();
        headers.put(HttpHeaderNames.USER_AGENT.toString(), "@polymarket/clob-client");
        headers.put(HttpHeaderNames.ACCEPT.toString(), "*/*");
        headers.put(HttpHeaderNames.CONNECTION.toString(), "keep-alive");
        headers.put(HttpHeaderNames.CONTENT_TYPE.toString(), JSON.toString());
        OVERLOAD_HEADERS_BY_NAME = Collections.unmodifiableMap(headers);
    }

    private final String baseUrl;
    private final List<Interceptor> interceptors;
    private final OkHttpClient client;
    private final ObjectMapper objectMapper;
    private final ExecutorService executorService;
    private final Request defaultRequest;

    // Primary Constructor
    public AbstractApi(
            String baseUrl,
            List<Interceptor> interceptors,
            OkHttpClient client,
            ObjectMapper objectMapper,
            ExecutorService executorService
    ) {
        this.baseUrl = baseUrl;
        this.interceptors = interceptors;
        this.client = client;
        this.objectMapper = objectMapper;
        this.executorService = executorService;

        // Build default request with base headers
        Request.Builder requestBuilder = new Request.Builder().url(baseUrl);
        OVERLOAD_HEADERS_BY_NAME.forEach(requestBuilder::addHeader);
        this.defaultRequest = requestBuilder.build();
    }

    // Overloaded Constructor with Defaults
    public AbstractApi(String baseUrl, List<Interceptor> interceptors) {
        this(
                baseUrl,
                interceptors,
                createDefaultClient(interceptors),
                new ObjectMapper(),
                Executors.newVirtualThreadPerTaskExecutor() // Or Executors.newCachedThreadPool() if pre-Java 21
        );
    }

    public AbstractApi(String baseUrl) {
        this(baseUrl, Collections.emptyList());
    }

    private static OkHttpClient createDefaultClient(List<Interceptor> interceptors) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.MILLISECONDS);
        if (interceptors != null) {
            interceptors.forEach(builder::addInterceptor);
        }
        return builder.build();
    }

    // --- Async HTTP Methods ---

    /**
     * Performs an asynchronous HTTP POST request.
     */
    public <T> CompletableFuture<T> post(String path, Object body, Class<T> responseType) {
        return CompletableFuture.supplyAsync(() -> {
            Request request = defaultRequest.newBuilder()
                    .url(baseUrl + path)
                    .post(toRequestBody(body))
                    .build();
            return executeRequest(request, responseType);
        }, executorService);
    }

    /**
     * Overload supporting complex types (like List<T>) using Jackson TypeReference.
     */
    public <T> CompletableFuture<T> post(String path, Object body, TypeReference<T> responseType) {
        return CompletableFuture.supplyAsync(() -> {
            Request request = defaultRequest.newBuilder()
                    .url(baseUrl + path)
                    .post(toRequestBody(body))
                    .build();
            return executeRequest(request, responseType);
        }, executorService);
    }

    /**
     * Performs an asynchronous HTTP GET request.
     */
    public <T> CompletableFuture<T> get(String path, Class<T> responseType) {
        return CompletableFuture.supplyAsync(() -> {
            Request request = defaultRequest.newBuilder()
                    .url(baseUrl + path)
                    .get()
                    .build();
            return executeRequest(request, responseType);
        }, executorService);
    }

    public <T> CompletableFuture<T> get(String path, TypeReference<T> responseType) {
        return CompletableFuture.supplyAsync(() -> {
            Request request = defaultRequest.newBuilder()
                    .url(baseUrl + path)
                    .get()
                    .build();
            return executeRequest(request, responseType);
        }, executorService);
    }

    public <T> CompletableFuture<List<T>> getQuery(String path, Map<String, String> queryParams, TypeReference<List<T>> responseType) {
        return CompletableFuture.supplyAsync(() -> {
            HttpUrl.Builder urlBuilder = HttpUrl.parse(baseUrl + path).newBuilder();
            queryParams.forEach(urlBuilder::addQueryParameter);
            Request request = defaultRequest.newBuilder()
                    .url(urlBuilder.build())
                    .build();
            return executeRequest(request, responseType);
        }, executorService);
    }

    /**
     * Performs an asynchronous HTTP DELETE request.
     */
    public <T> CompletableFuture<T> delete(String path, Object body, Class<T> responseType) {
        return CompletableFuture.supplyAsync(() -> {
            Request request = defaultRequest.newBuilder()
                    .url(baseUrl + path)
                    .delete(toRequestBody(body))
                    .build();
            return executeRequest(request, responseType);
        }, executorService);
    }

    public <T> CompletableFuture<T> delete(String path, Object body, TypeReference<T> responseType) {
        return CompletableFuture.supplyAsync(() -> {
            Request request = defaultRequest.newBuilder()
                    .url(baseUrl + path)
                    .delete(toRequestBody(body))
                    .build();
            return executeRequest(request, responseType);
        }, executorService);
    }

    // --- Request Execution Core ---

    /**
     * Executes an HTTP request and deserializes the response using a Class type.
     */
    public <T> T executeRequest(Request request, Class<T> responseType) {
        try (Response response = client.newCall(request).execute()) {
            ResponseBody responseBody = response.body();
            String body = responseBody != null ? responseBody.string() : "";

            if (!response.isSuccessful()) {
                throw new ApiException(
                        "Request failed: " + response.code() + " - " + body,
                        HttpResponseStatus.valueOf(response.code())
                );
            }
            return objectMapper.readValue(body, responseType);
        } catch (IOException e) {
            throw new RuntimeException("Network communication failure", e);
        }
    }

    /**
     * Executes an HTTP request and deserializes the response using a Jackson TypeReference.
     */
    public <T> T executeRequest(Request request, TypeReference<T> responseType) {
        try (Response response = client.newCall(request).execute()) {
            ResponseBody responseBody = response.body();
            String body = responseBody != null ? responseBody.string() : "";

            if (!response.isSuccessful()) {
                throw new ApiException(
                        "Request failed: " + response.code() + " - " + body,
                        HttpResponseStatus.valueOf(response.code())
                );
            }
            return objectMapper.readValue(body, responseType);
        } catch (IOException e) {
            throw new RuntimeException("Network communication failure", e);
        }
    }

    /**
     * Converts any object to a JSON RequestBody.
     */
    private RequestBody toRequestBody(Object body) {
        if (body == null) {
            return RequestBody.create("", null); // Maps to RequestBody.EMPTY equivalent
        }
        try {
            String json = objectMapper.writeValueAsString(body);
            return RequestBody.create(json, JSON);
        } catch (Exception e) {
            throw new RuntimeException("Serialization failure", e);
        }
    }

    // --- Getters ---
    public String getBaseUrl() { return baseUrl; }
    public List<Interceptor> getInterceptors() { return interceptors; }
    public OkHttpClient getClient() { return client; }
    public ObjectMapper getObjectMapper() { return objectMapper; }
    public ExecutorService getExecutorService() { return executorService; }
}
