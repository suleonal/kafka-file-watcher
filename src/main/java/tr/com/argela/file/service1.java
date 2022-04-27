package tr.com.argela.file;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Service
public class service1 {

    private static final Logger logger = LoggerFactory.getLogger(service1.class);

    @Value("${file.path}")
    String filePath;

    @Value(value = "${kafka.topic1}")
    private String topicName;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public service1(){
        initService();
    }

    private void initService() {
        Thread thread = new Thread(run);
        thread.start();
    }

    public void onNewFile(String filePath) {
       logger.info("[service1][onNewFile] "+ filePath);
        ListenableFuture<SendResult<String, String>> future = this.kafkaTemplate.send(topicName, filePath);

        future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
            @Override
            public void onSuccess(SendResult<String, String> result) {
            }

            @Override
            public void onFailure(Throwable ex) {
                logger.error("Unable to send file path : " + filePath, ex);
            }
        });
    }
//dene
    Runnable run = new Runnable() {
        public void run() {
            String listenFolder= "/sule/incoming";

            while (true) {
                
                File f = new File(listenFolder);
                String[] files = f.list();
                System.out.println("[service1][watchDir] "+files.length);
                for (String fileName : files) {
                    onNewFile(listenFolder+File.separator+fileName);
                }
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                }
            }
        }
    };
 

}
