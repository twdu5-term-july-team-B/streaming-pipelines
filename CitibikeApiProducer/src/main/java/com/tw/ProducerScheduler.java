package com.tw;

import com.tw.services.ApiProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
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

    @Autowired
    private RestTemplate restTemplate = restTemplate();

    @Value("${producer.url}")
    private String url;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Scheduled(cron="${producer.cron}")
    public void scheduledProducer() throws IOException {
        HttpEntity<String> response;

        if(url.startsWith("mock")) {
            response = new HttpEntity<>(readTestData(url));
        }
        else{
           response = restTemplate.exchange(url, HttpMethod.GET, HttpEntity.EMPTY, String.class);
        }
        apiProducer.sendMessage(response);
    }

    public static String readTestData(String url) throws IOException {
        InputStream is = ProducerScheduler.class.getResourceAsStream("/" + url);
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
