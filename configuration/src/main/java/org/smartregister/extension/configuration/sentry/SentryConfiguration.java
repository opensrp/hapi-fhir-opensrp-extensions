/*
 * Copyright 2022 Ona Systems, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.smartregister.extension.configuration.sentry;

import io.sentry.Sentry;
import io.sentry.SentryOptions;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.VisibleForTesting;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

@ConditionalOnProperty(
        prefix = "sentry",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = false)
@Configuration
public class SentryConfiguration {

    @Value("${sentry.options.dsn:}")
    private String dsn;

    @Value("${sentry.options.release:}")
    private String release;

    @Value("${sentry.options.environment:}")
    private String environment;

    @Value("#{${sentry.options.tags: {:}} ?: {:} }")
    private Map<String, String> tags;

    @Value("${sentry.options.debug: false}")
    private boolean debug;

    @PostConstruct
    public void initialize() {
        if (dsn != null && !dsn.trim().isEmpty()) {
            initializeSentry();
        }
    }

    @VisibleForTesting
    protected void initializeSentry() {
        Sentry.init(
                sentryOptions -> {
                    sentryOptions.setDsn(dsn);
                    sentryOptions.setRelease(release);
                    sentryOptions.setEnvironment(environment);
                    sentryOptions.setDebug(debug);
                    populateTags(sentryOptions);
                });
    }

    @VisibleForTesting
    protected void populateTags(SentryOptions sentryOptions) {
        try {
            for (Map.Entry<String, String> extraTagsEntry : tags.entrySet()) {
                String key = extraTagsEntry.getKey();
                if (key != null && !key.trim().isEmpty())
                    sentryOptions.setTag(extraTagsEntry.getKey(), extraTagsEntry.getValue());
            }
        } catch (Exception e) {
            LogFactory.getLog(this.getClass()).error(e);
        }
    }
}
