package demo

class TopicController {

    TopicService topicService

    def create(String topicId) {
        String result = topicService.createTopicExample(topicId)

    }

    def index() {

    }
}
