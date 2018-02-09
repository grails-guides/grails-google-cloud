package demo

class SubscriberController {

    SubscriberService subscriberService

    def index() { }

    def subscriberDemo(String subscriberId) {
        subscriberService.subscribeExample(subscriberId)
    }

}
