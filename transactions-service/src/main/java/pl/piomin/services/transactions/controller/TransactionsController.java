package pl.piomin.services.transactions.controller;

import java.util.concurrent.ExecutionException;
import org.springframework.web.bind.annotation.*;
import pl.piomin.services.transactions.domain.OrderGroup;
import pl.piomin.services.transactions.producer.TransactionsProducer;
import pl.piomin.services.transactions.repository.OrderGroupRepository;

import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionsController {

    TransactionsProducer producer;
    OrderGroupRepository repository;

    public TransactionsController(TransactionsProducer producer, OrderGroupRepository repository) {
        this.producer = producer;
        this.repository = repository;
    }

    @PostMapping
    public void sendTransaction(@RequestBody boolean error) throws InterruptedException, ExecutionException {
        producer.sendOrderGroup(error);
    }

    @GetMapping
    public List<OrderGroup> findAll() {
        return (List<OrderGroup>) repository.findAll();
    }
}
