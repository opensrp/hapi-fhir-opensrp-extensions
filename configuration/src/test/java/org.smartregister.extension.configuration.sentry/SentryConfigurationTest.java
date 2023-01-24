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

import static org.mockito.Mockito.*;

import io.sentry.Sentry;
import io.sentry.SentryOptions;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.internal.WhiteboxImpl;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Sentry.class)
public class SentryConfigurationTest {

	private SentryConfiguration sentryConfiguration;

	@Before
	public void setUp() {
		sentryConfiguration = spy(new SentryConfiguration());
	}

	@Test
	public void testInitializeShouldNotInitializeSentryIfDsnIsEmpty() {
		WhiteboxImpl.setInternalState(sentryConfiguration, "dsn", "");
		sentryConfiguration.initialize();
		verify(sentryConfiguration, never()).initializeSentry();
	}

	@Test
	public void testInitializeShouldInitializeSentryIfDsnIsNotEmpty() {
		PowerMockito.mockStatic(Sentry.class);
		WhiteboxImpl.setInternalState(
				sentryConfiguration, "dsn", "https://examplePublicKey.sdsd.w/0");
		sentryConfiguration.initialize();
		verify(sentryConfiguration, atMostOnce()).initializeSentry();
	}

	@Test
	public void testPopulateTagsShouldNotAddTagsIfNotPresent() {
		WhiteboxImpl.setInternalState(sentryConfiguration, "tags", new HashMap<>());
		SentryOptions sentryOptions = mock(SentryOptions.class);
		sentryConfiguration.populateTags(sentryOptions);
		verify(sentryOptions, never()).setTag(anyString(), anyString());
	}

	@Test
	public void testPopulateTagsShouldAddTagsToSentryOptions() {
		String releaseName = "release-name";
		String release = "release-a";
		Map<String, String> map = new HashMap<>();
		map.put(releaseName, release);
		WhiteboxImpl.setInternalState(sentryConfiguration, "tags", map);
		SentryOptions sentryOptions = mock(SentryOptions.class);
		sentryConfiguration.populateTags(sentryOptions);
		verify(sentryOptions, only()).setTag(eq(releaseName), eq(release));
	}
}
