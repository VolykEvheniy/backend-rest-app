package com.vlkevheniy.carmanagement.service;

import com.vlkevheniy.carmanagement.dto.MessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailProducerService {

    @Value("${kafka.topic.messageReceived}")
    private String messageReceivedTopic;

    private final KafkaOperations<String, MessageDto> kafkaOperations;

    public void sendMessage(MessageDto message) {
        kafkaOperations.send(messageReceivedTopic, message);
    }
}
