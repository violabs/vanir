package io.violabs.freyr.config

import org.apache.kafka.clients.admin.NewTopic
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("app.kafka")
class AppKafkaProperties(var orderTopic: String) {
    fun newOrderTopic(): NewTopic = NewTopic(orderTopic, 1, 1.toShort())
}