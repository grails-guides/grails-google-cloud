package demo

import com.google.cloud.ServiceOptions
import com.google.cloud.pubsub.v1.AckReplyConsumer
import com.google.cloud.pubsub.v1.MessageReceiver
import com.google.cloud.pubsub.v1.Subscriber
import com.google.pubsub.v1.PubsubMessage
import com.google.pubsub.v1.SubscriptionName
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
}
