package demo

import com.google.api.gax.batching.BatchingSettings
import com.google.api.gax.retrying.RetrySettings
import com.google.api.gax.rpc.ApiException
import com.google.cloud.ServiceOptions
import com.google.cloud.pubsub.v1.MessageWaiter
import com.google.cloud.pubsub.v1.Publisher
import com.google.cloud.pubsub.v1.TopicAdminClient
import com.google.pubsub.v1.TopicName
import grails.gorm.transactions.Transactional
import io.grpc.CallCredentials
import io.grpc.Channel

import javax.annotation.Nullable
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.locks.Lock

@Transactional
class TopicService {

    private final TopicName topicName;
    private final String cachedTopicNameString;

    private final BatchingSettings batchingSettings;
    private final RetrySettings retrySettings;
    private final Publisher.LongRandom longRandom;

    private final Lock messagesBatchLock;
    private List<Publisher.OutstandingPublish> messagesBatch;
    private int batchedBytes;

    private final AtomicBoolean activeAlarm;

    private final Channel channel;
    @Nullable private final CallCredentials callCredentials;

    private final ScheduledExecutorService executor;
    private final AtomicBoolean shutdown;
    private final List<AutoCloseable> closeables = new ArrayList<>();
    private final MessageWaiter messagesWaiter;
    private ScheduledFuture<?> currentAlarmFuture;

    def serviceMethod() {

    }

    def createTopicExample(String topicId) {

        // Your Google Cloud Platform project ID
        String projectId = ServiceOptions.getDefaultProjectId();

        // Create a new topic
        TopicName topic = TopicName.of(projectId, topicId);
        try {
            TopicAdminClient topicAdminClient = TopicAdminClient.create()
            topicAdminClient.createTopic(topic);
        } catch (ApiException e) {
            // example : code = ALREADY_EXISTS(409) implies topic already exists
            System.out.print(e.getStatusCode().getCode());
            System.out.print(e.isRetryable());
        }

        System.out.printf("Topic %s:%s created.\n", topic.getProject(), topic.getTopic());

    }
}
