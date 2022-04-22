package tr.com.argela.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class service4 {

    private static final Logger logger = LoggerFactory.getLogger(service1.class);

    @Value("${file.path}")
    String filePath;

    @KafkaListener(topics = "${kafka.topic3}", groupId = "${kafka.group}")
    public void onFileProcossed(String filePath) {
        logger.info("New file path: " + filePath);
    }

}
