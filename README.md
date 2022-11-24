transaction-service向transactions topic发送Order消息；
account-service消费transactions topic的Order消息，完成转账、修改账户余额数据并保存至数据库，改变Order的状态，并向orders topic发布Order消息；
transaction-service消费orders topic的Order消息，更新OrderGroup至数据库；

(1)set the spring.kafka.producer.transaction-id-prefix property to enable transactions;
(2)add @Transactional

测试：
（1）理想：向transactions topic发送Order消息失败，然后OrderGroup存的数据应该回滚；
（2）消费消息时，

(1)producer发送消息，失败；read-committed的consumer读不到一切数据；
(2)producer发送消息，失败；read-uncommitted的consumer可以读到一切数据；
(3)producer发送消息，成功，只有当提交事务之后，read-committed的consumer才能读到数据；

It is recommended to use exactly-once processing along with the batch consumption mode. While it is possible to use it with a single Kafka message, it'll have a significant performance impact.
https://smallrye.io/smallrye-reactive-messaging/3.18.0/kafka/transactions/#exactly-once-processing
CREATE TABLE order_group (
id int IDENTITY(1,1) NOT NULL,
status varchar(15),
total_no_of_orders int,
processed_no_of_orders int);


CREATE TABLE account (
id bigint IDENTITY(1,1) NOT NULL,
balance int
)