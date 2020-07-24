package com.tw;

import com.tw.services.ApiProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.io.*;

@Component
public class ProducerScheduler {

    @Autowired
    private ApiProducer apiProducer;

    @Value("${producer.url}")
    private String url;

    @Scheduled(cron="${producer.cron}")
    public void scheduledProducer(RestTemplate restTemplate) throws IOException {
        HttpEntity<String> response;

        if(url.equals("testurl")) {
            response = new HttpEntity<>(readTestData());
        }
        else{
           response = restTemplate.exchange(url, HttpMethod.GET, HttpEntity.EMPTY, String.class);
        }
        apiProducer.sendMessage(response);
    }

    public static String readTestData() throws IOException {
        InputStream is = ProducerScheduler.class.getResourceAsStream("/mock_response_data");
        BufferedReader buf = new BufferedReader(new InputStreamReader(is));
        String line = buf.readLine();
        StringBuilder sb = new StringBuilder();
        while(line != null){
            sb.append(line);
            line = buf.readLine();
        }
        return sb.toString();
    }
}
