package pl.piomin.services.transactions.producer;

import java.util.concurrent.ExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.concurrent.ListenableFuture;
import pl.piomin.services.common.model.Order;
import pl.piomin.services.transactions.callback.TransactionsResultCallback;
import pl.piomin.services.transactions.domain.OrderGroup;
import pl.piomin.services.transactions.listener.OrdersListener;
import pl.piomin.services.transactions.repository.OrderGroupRepository;

@Service
public class TransactionsProducer {

    long id = 1;
    KafkaTemplate<Long, Order> kafkaTemplate;
    TransactionsResultCallback callback;
    OrderGroupRepository repository;

    private static final Logger LOG = LoggerFactory
            .getLogger(OrdersListener.class);

    public TransactionsProducer(KafkaTemplate<Long, Order> kafkaTemplate,
                                TransactionsResultCallback callback,
                                OrderGroupRepository repository) {
        this.kafkaTemplate = kafkaTemplate;
        this.callback = callback;
        this.repository = repository;
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public void sendOrderGroup(boolean error) throws InterruptedException, ExecutionException {
        OrderGroup og = repository.save(new OrderGroup("SENT", 10, 0));
        generateAndSendPackage(error, og.getId());
    }
    @Transactional("kafkaTransactionManager")
    public void generateAndSendPackage(boolean error, Long groupId)
            throws InterruptedException, ExecutionException {
        for (long i = 0; i < 10; i++) {
            Order o = new Order(id++, i+1, i+2, 1000, "NEW", groupId);
            ListenableFuture<SendResult<Long, Order>> result =
                    kafkaTemplate.send("transactions", o.getId(), o);
//            result.addCallback(callback);
            LOG.info("+++++++++++++++++++++");
            result.get();
            if (i == 7) {
                LOG.info("");
            }
//            if (error && i > 5) {
//                throw new RuntimeException();
//            }
            Thread.sleep(4000);
        }
        LOG.info("Produce successfully!");
    }

}
