package config;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;


public class RabbitMQConfig {

    public static final String TASK_EXCHANGE = "text_tasks";
    public static final String RESULT_EXCHANGE = "text_results";

    public static final String TASK_QUEUE = "text_task_queue";
    public static final String RESULT_QUEUE = "text_result_queue";

    public static final String TASK_ROUTING_KEY = "text";
    public static final String RESULT_ROUTING_KEY = "result";

    private final Connection connection;

    public RabbitMQConfig() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(5672);
        factory.setUsername("user");
        factory.setPassword("pass");

        this.connection = factory.newConnection();
    }

    public Connection getConnection() {
        return connection;
    }

    public void close() throws Exception {
        if (connection != null && connection.isOpen()) {
            connection.close();
        }
    }
}
