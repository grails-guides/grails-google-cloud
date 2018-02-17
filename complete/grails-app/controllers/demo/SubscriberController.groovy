package demo

import com.google.cloud.pubsub.v1.SubscriptionAdminClient
import com.google.pubsub.v1.PushConfig
import com.google.pubsub.v1.Subscription
import com.google.pubsub.v1.SubscriptionName
import com.google.pubsub.v1.TopicName

class SubscriberController {

    SubscriberService subscriberService

    def index() { }

    def subscriberDemo(String subscriptionId) {
        subscriberService.subscribeExample(subscriptionId)
    }

    def create(String topicId, String subscriptionId) {
        subscriberService.create(topicId,subscriptionId)
    }



}
