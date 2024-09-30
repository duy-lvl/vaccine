package fa.training.frontend.service;

import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

/**
 * @param <T>: Request Type
 * @param <U>: Response Type
 */
public class HttpServiceBase<T, U> {


    public U getFromAPI(String url, Class<U> responseType) {
        HttpEntity<T> requestEntity = new HttpEntity<T>(getHttpHeader());

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<U> response;

        try {
            response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, responseType);
        } catch (HttpClientErrorException exception) {
            System.out.println(exception.getMessage());
            return exception.getResponseBodyAs(responseType);
        }

        HttpStatusCode statusCode = response.getStatusCode();
        U results = null;
        if (statusCode.is2xxSuccessful()) {
            results = response.getBody();
        }
        return results;
    }

    public U deleteFromAPI(String url, Class<U> responseType) {
        HttpEntity<T> requestEntity = new HttpEntity<T>(getHttpHeader());

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<U> response;

        try {
            response = restTemplate.exchange(url, HttpMethod.DELETE, requestEntity, responseType);
        } catch (HttpClientErrorException exception) {
            System.out.println(exception.getMessage());
            return exception.getResponseBodyAs(responseType);
        }

        HttpStatusCode statusCode = response.getStatusCode();
        U results = null;
        if (statusCode.is2xxSuccessful()) {
            results = response.getBody();
        }
        return results;
    }

    public U postToAPI(T requestObject, String url, Class<U> responseType) {
        return postOrPutToAPI(requestObject, url, responseType, HttpMethod.POST);
    }

    public U putToAPI(T requestObject, String url, Class<U> responseType) {
        return postOrPutToAPI(requestObject, url, responseType, HttpMethod.PUT);
    }

    private U postOrPutToAPI(T requestObject, String url, Class<U> responseType, HttpMethod httpMethod) {
        HttpEntity<T> requestEntity = new HttpEntity<T>(requestObject, getHttpHeader());

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<U> response = null;
        try {
            response = restTemplate.exchange(url, httpMethod, requestEntity, responseType);
        } catch (HttpClientErrorException e) {
            System.out.println(e.getMessage());
            return e.getResponseBodyAs(responseType);
        }

        HttpStatusCode statusCode = response.getStatusCode();
        U results = null;
        if (statusCode.is2xxSuccessful()) {
            results = response.getBody();
        }
        return results;
    }
    public HttpHeaders getHttpHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(new MediaType[] { MediaType.APPLICATION_JSON }));
        headers.setContentType(MediaType.APPLICATION_JSON);
        if(!JwtToken.bearerToken.isEmpty()){
            headers.set(JwtToken.HEADER, JwtToken.bearerToken);
        }
        return headers;
    }
}
