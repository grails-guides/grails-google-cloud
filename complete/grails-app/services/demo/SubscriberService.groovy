package demo

import com.google.api.core.ApiFuture
import com.google.cloud.ServiceOptions
import com.google.cloud.pubsub.v1.AckReplyConsumer
import com.google.cloud.pubsub.v1.MessageReceiver
import com.google.cloud.pubsub.v1.Publisher
import com.google.cloud.pubsub.v1.Subscriber
import com.google.cloud.pubsub.v1.SubscriptionAdminClient
import com.google.protobuf.ByteString
import com.google.pubsub.v1.PubsubMessage
import com.google.pubsub.v1.PushConfig
import com.google.pubsub.v1.Subscription
import com.google.pubsub.v1.SubscriptionName
import com.google.pubsub.v1.TopicName
import grails.core.GrailsApplication
import grails.gorm.transactions.Transactional

import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingDeque

@Transactional
class SubscriberService {

    def serviceMethod() {

    }
    // use the default project id
    private String PROJECT_ID = ServiceOptions.getDefaultProjectId();
    GrailsApplication grailsApplication

    private static final BlockingQueue<PubsubMessage> messages = new LinkedBlockingDeque<>();

    static class MessageReceiverExample implements MessageReceiver {

        @Override
        public void receiveMessage(PubsubMessage message, AckReplyConsumer consumer) {
            messages.offer(message);
            consumer.ack();
        }
    }

    /** Receive messages over a subscription. */
    public void subscribeExample(String subscriptionId) throws Exception {
        // set subscriber id, eg. my-sub
        if (!PROJECT_ID) {
            PROJECT_ID = grailsApplication.config.get("googlecloud.projectid")
        }
        SubscriptionName subscriptionName = SubscriptionName.of(PROJECT_ID, subscriptionId);
        Subscriber subscriber = null;
        try {
            // create a subscriber bound to the asynchronous message receiver
            subscriber =
                    Subscriber.newBuilder(subscriptionName, new MessageReceiverExample()).build();
            subscriber.startAsync().awaitRunning();
            // Continue to listen to messages
            while (true) {
                PubsubMessage message = messages.take();
                System.out.println("Message Id: " + message.getMessageId());
                System.out.println("Data: " + message.getData().toStringUtf8());
            }
        } finally {
            if (subscriber != null) {
                subscriber.stopAsync();
            }
        }
    }

    def create(String topicId, String subscriptionId) {
        try {
            SubscriptionAdminClient subscriptionAdminClient = SubscriptionAdminClient.create()
            // eg. projectId = "my-test-project", topicId = "my-test-topic"
            if (!PROJECT_ID) {
                PROJECT_ID = grailsApplication.config.get("googlecloud.projectid")
            }
            TopicName topicName = TopicName.of(PROJECT_ID, topicId);
            // eg. subscriptionId = "my-test-subscription"
            SubscriptionName subscriptionName =
                    SubscriptionName.of(PROJECT_ID, subscriptionId);
            // create a pull subscription with default acknowledgement deadline
            Subscription subscription =
                    subscriptionAdminClient.createSubscription(
                            subscriptionName, topicName, PushConfig.getDefaultInstance(), 0);
            return subscription;
        } catch (Exception e) {
            log.error(e.getMessage(),e)
        }
    }

    def sendMessages() {
        TopicName topicName = TopicName.of("my-project-id", "my-topic-id");
        Publisher publisher = null;
        List<ApiFuture<String>> messageIdFutures = new ArrayList<>();

        try {
            // Create a publisher instance with default settings bound to the topic
            publisher = Publisher.newBuilder(topicName).build();

            List<String> messages = Arrays.asList("first message", "second message");

            // schedule publishing one message at a time : messages get automatically batched
            for (String message : messages) {
                ByteString data = ByteString.copyFromUtf8(message);
                PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(data).build();

                // Once published, returns a server-assigned message id (unique within the topic)
                ApiFuture<String> messageIdFuture = publisher.publish(pubsubMessage);
                messageIdFutures.add(messageIdFuture);
            }
        } finally {
            // wait on any pending publish requests.
            List<String> messageIds = ApiFutures.allAsList(messageIdFutures).get();

            for (String messageId : messageIds) {
                System.out.println("published with message ID: " + messageId);
            }

            if (publisher != null) {
                // When finished with the publisher, shutdown to free up resources.
                publisher.shutdown();
            }
        }
    }
}
