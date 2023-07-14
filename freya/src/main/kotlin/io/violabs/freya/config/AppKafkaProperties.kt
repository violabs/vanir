package io.violabs.freya.config

import org.apache.kafka.clients.admin.NewTopic
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("app.kafka")
class AppKafkaProperties(
    var userTopic: String
) {
    fun newUserTopic(): NewTopic = NewTopic(userTopic, 1, 1.toShort())
}