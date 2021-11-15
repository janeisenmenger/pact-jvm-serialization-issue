import au.com.dius.pact.provider.MessageAndMetadata;
import au.com.dius.pact.provider.PactVerifyProvider;
import au.com.dius.pact.provider.junit5.MessageTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.Consumer;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.loader.PactFolder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.HashMap;

@Provider("MyProvider")
@Consumer("MyConsumer")
@PactFolder("pacts")
public class MessageProviderTest {
    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    void testTemplate(PactVerificationContext context) {
        context.verifyInteraction();
    }

    @BeforeEach
    void before(PactVerificationContext context) {
        context.setTarget(new MessageTestTarget());
    }

    @PactVerifyProvider("a message")
    public MessageAndMetadata generateAMessage() {
        HashMap<String, Object> metadata = new HashMap<>();
        metadata.put("kafka_topic", "project-validation-stage-topic");
        metadata.put("content-type", "application/json");

        String payload = "{\"payload\": \"my-payload\"}";

        return new MessageAndMetadata(payload.getBytes(), metadata);
    }
}