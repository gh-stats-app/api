package app.ghstats.api.services.metrics;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.graphite.GraphiteReporter;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.dropwizard.DropwizardClock;
import io.micrometer.graphite.GraphiteConfig;
import io.micrometer.graphite.GraphiteHierarchicalNameMapper;
import io.micrometer.graphite.GraphiteMeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {

    @Bean
    MetricRegistry metricRegistry() {
        return new MetricRegistry();
    }

    @Bean
    public GraphiteMeterRegistry graphiteMeterRegistry(MetricRegistry metricRegistry, Clock clock, GraphiteConfig config, GraphiteConfigurationProperties configurationProperties) {
        GraphiteHttpSender graphite = new GraphiteHttpSender(configurationProperties);
        GraphiteReporter graphiteReporter = GraphiteReporter.forRegistry(metricRegistry)
                .withClock(new DropwizardClock(clock))
                .convertRatesTo(config.rateUnits())
                .convertDurationsTo(config.durationUnits())
                .addMetricAttributesAsTags(config.graphiteTagsEnabled())
                .build(graphite);
        return new GraphiteMeterRegistry(config, clock, new GraphiteHierarchicalNameMapper(
                config.tagsAsPrefix()),
                metricRegistry,
                graphiteReporter
        );
    }

}