package io.violabs.freya.controller

import io.violabs.core.domain.UserMessage
import io.violabs.freya.message.UserProducer
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/kafka")
class KafkaController(private val userProducer: UserProducer) {

    @PostMapping
    suspend fun sendUserMessage(@RequestBody userMessage: UserMessage) = userProducer.sendMessage(userMessage)
}