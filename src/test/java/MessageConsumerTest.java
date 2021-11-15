import au.com.dius.pact.consumer.MessagePactBuilder;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.junit5.*;
import au.com.dius.pact.core.model.PactSpecVersion;
import au.com.dius.pact.core.model.annotations.Pact;
import au.com.dius.pact.core.model.annotations.PactDirectory;
import au.com.dius.pact.core.model.messaging.Message;
import au.com.dius.pact.core.model.messaging.MessagePact;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(PactConsumerTestExt.class)
@PactDirectory("pacts")
@PactTestFor(providerName = "MyProvider", pactVersion = PactSpecVersion.V3, providerType = ProviderType.ASYNCH)
public class MessageConsumerTest {

    @Pact(consumer="MyConsumer", provider = "MyProvider")
    MessagePact createPact(MessagePactBuilder builder) {
        PactDslJsonBody body = new PactDslJsonBody();
        body.stringValue("payload", "my-payload");

        return builder.expectsToReceive("a message")
                .withMetadata(metadata -> {
                    metadata.add("content-type", "application/json");
                    metadata.matchRegex("kafka_topic", "project-(production|validation)-stage-topic", "project-production-stage-topic");
                })
                .withContent(body)
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "createPact")
    void test(List<Message> messages) {
        assertEquals(new String(messages.get(0).contentsAsBytes()), "{\"payload\":\"my-payload\"}");
        assertEquals(messages.get(0).getMetadata().get("kafka_topic"), "project-production-stage-topic");
    }
}