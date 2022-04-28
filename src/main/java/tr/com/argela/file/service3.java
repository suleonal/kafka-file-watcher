package tr.com.argela.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

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
public class service3 {

    private static final Logger logger = LoggerFactory.getLogger(service3.class);

    @Value("${file.path}")
    String filePath;

    @Value(value = "${kafka.topic3}")
    private String topicName;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void onFileComplete(String filePath) {
        ListenableFuture<SendResult<String, String>> future = this.kafkaTemplate.send(topicName, filePath);

        future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
            @Override
            public void onSuccess(SendResult<String, String> result) {
                logger.info("[Service3][onFileComplete] " + filePath);
            }

            @Override
            public void onFailure(Throwable ex) {
                logger.error("Unable to send file path : " + filePath, ex);
            }
        });
    }

    @KafkaListener(topics = "${kafka.topic2}", groupId = "${kafka.group}")
    public void onFileProcessed(String filePath) {
        logger.info("[Service3][onFileProcessed] " + filePath);
        appendtoFile(filePath);
    }

    public void appendtoFile(String filePath) {

        File newFile = new File(filePath);
        File destFolder = new File("/sule/complete");
        File destFile = new File(destFolder.getPath() + File.separator + newFile.getName());
        try {
            String c = "hello";
            FileWriter yazici = new FileWriter(filePath, true);
            BufferedWriter yaz = new BufferedWriter(yazici);
            /*PrintWriter out = new PrintWriter(yaz);
            out.println("hello");*/
            yaz.write(c);
            yaz.close();
            Files.move(Paths.get(newFile.getPath()), Paths.get(destFile.getPath()),
                    StandardCopyOption.REPLACE_EXISTING);
            onFileProcessed(destFile.getPath());
        } catch (IOException e) {
            logger.error("[Service3][move][Failed]" + newFile + " to " + destFile + ", msg:" + e.getMessage());
        }
    }
}
