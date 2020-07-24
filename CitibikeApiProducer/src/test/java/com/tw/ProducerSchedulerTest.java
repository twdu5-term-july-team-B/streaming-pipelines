package com.tw;

import com.tw.services.ApiProducer;
import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;
import java.io.*;

import org.mockito.Answers;
import org.mockito.InjectMocks;
import static org.mockito.Mockito.*;

import org.mockito.Mock;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;


@RunWith(SpringJUnit4ClassRunner.class)
public class ProducerSchedulerTest {

    @Mock
    private ApiProducer apiProducer;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private HttpEntity<String> httpEntity;

    @InjectMocks
    private ProducerScheduler producerScheduler;

    @Before
    public void setUp() {
        this.producerScheduler = new ProducerScheduler(apiProducer,restTemplate,httpEntity);
    }

    @Test
    public void schedulerShouldSendMockResponseIfReceivesTestUrl() throws IOException {
        ReflectionTestUtils.setField(producerScheduler, "url", "testurl");
        String mock_data = ProducerScheduler.readTestData();

        producerScheduler.scheduledProducer(restTemplate);

        verify(apiProducer).sendMessage(new HttpEntity<>(mock_data));

    }

    @Test
    public void schedulerShouldSendApiResponseIfDoesNotReceiveTestUrl() throws IOException{
        ReflectionTestUtils.setField(producerScheduler, "url", "realurl");

        restTemplate = mock(RestTemplate.class, Answers.RETURNS_DEEP_STUBS);
        ResponseEntity<String> responseEntity = new ResponseEntity<>("Hello", HttpStatus.OK);
        when(restTemplate.exchange("realurl", HttpMethod.GET, HttpEntity.EMPTY, String.class)).thenReturn(responseEntity);
        producerScheduler.scheduledProducer(restTemplate);


        verify(apiProducer).sendMessage(responseEntity);
    }

    @Test
    public void readTestDataShouldAccuratelyReturnStringOfTestData() throws IOException{
        String fakeData = ProducerScheduler.readTestData();
        System.out.println(fakeData.length());
        assert(fakeData.length() == 935);
    }
}