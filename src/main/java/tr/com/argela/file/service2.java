package tr.com.argela.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Service
public class service2 {

    private static final Logger logger = LoggerFactory.getLogger(service1.class);

    @Value("${file.path}")
    String filePath;

    @Value(value = "${kafka.topic2}")
    private String topicName;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void onFileProcossed(String filePath) {
        ListenableFuture<SendResult<String, String>> future = this.kafkaTemplate.send(topicName, filePath);

        future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
            @Override
            public void onSuccess(SendResult<String, String> result) {
                logger.info("Sent file path: " + filePath
                        + " with offset: " + result.getRecordMetadata().offset());
            }

            @Override
            public void onFailure(Throwable ex) {
                logger.error("Unable to send file path : " + filePath, ex);
            }
        });
    }

    @KafkaListener(topics = "${kafka.topic1}", groupId = "${kafka.group}")
    public void onNewFile(String filePath) {
        logger.info("New file path: " + filePath);
        move(filePath);
    }

    public void move(String filePath) {
        onFileProcossed(filePath);
    }
}