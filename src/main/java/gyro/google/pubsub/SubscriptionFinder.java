package gyro.google.pubsub;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.api.gax.rpc.NotFoundException;
import com.google.cloud.pubsub.v1.SubscriptionAdminClient;
import com.google.pubsub.v1.ProjectName;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.Subscription;
import gyro.core.Type;
import gyro.google.GoogleFinder;

/**
 * Query for subscriptions.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *    subscription: $(external-query google::subscription {name: "subscription-example"})
 */
@Type("subscription")
public class SubscriptionFinder extends GoogleFinder<SubscriptionAdminClient, Subscription, SubscriptionResource> {

    private String name;

    /**
     * The name of the subscription.
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    protected List<Subscription> findAllGoogle(SubscriptionAdminClient client) throws Exception {
        List<Subscription> subscriptions = new ArrayList<>();

        try {
            client.listSubscriptions(ProjectName.format(getProjectId())).iterateAll().forEach(subscriptions::add);
        } catch (NotFoundException ignore) {
            // Either topic or subscription not found
        } finally {
            client.shutdownNow();
        }

        return subscriptions;
    }

    @Override
    protected List<Subscription> findGoogle(
        SubscriptionAdminClient client, Map<String, String> filters) throws Exception {
        List<Subscription> subscriptions = new ArrayList<>();

        try {
            Subscription subscription = client.getSubscription(ProjectSubscriptionName.format(
                getProjectId(),
                filters.get("name")));
            subscriptions.add(subscription);
        } catch (NotFoundException ignore) {
            // Subscription not found
        } finally {
            client.shutdownNow();
        }

        return subscriptions;
    }
}
