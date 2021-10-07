package app.ghstats.api.services.metrics;

import com.codahale.metrics.graphite.GraphiteSender;
import com.google.common.collect.Lists;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.util.List;

class GraphiteHttpSender implements GraphiteSender {

    private final RestTemplate client;
    private final List<GraphiteMetric> metrics = Lists.newArrayList();
    private final GraphiteConfigurationProperties graphiteConfigurationProperties;

    GraphiteHttpSender(GraphiteConfigurationProperties graphiteConfigurationProperties) {
        this.graphiteConfigurationProperties = graphiteConfigurationProperties;
        this.client = new RestTemplate();
    }

    @Override
    public void connect() throws IllegalStateException {
        // Just no op here
    }

    @Override
    public void close() {
        // no op
    }

    @Override
    public void send(String name, String value, long timestamp) {
        metrics.add(new GraphiteMetric(name, 10, Double.parseDouble(value), timestamp));
    }

    @Override
    public void flush() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(graphiteConfigurationProperties.instance(), graphiteConfigurationProperties.token());
        client.exchange(graphiteConfigurationProperties.endpoint(), HttpMethod.POST, new HttpEntity<>(metrics, headers), Void.class);
        metrics.clear();
    }

    @Override
    public boolean isConnected() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int getFailures() {
        // TODO Auto-generated method stub
        return 0;
    }

    private record GraphiteMetric(String name, int interval, double value, long time) {
    }
}