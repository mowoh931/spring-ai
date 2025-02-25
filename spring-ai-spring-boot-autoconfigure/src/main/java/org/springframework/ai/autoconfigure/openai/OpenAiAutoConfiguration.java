/*
 * Copyright 2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.ai.autoconfigure.openai;

import org.springframework.ai.autoconfigure.NativeHints;
import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.ai.openai.OpenAiEmbeddingClient;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

@AutoConfiguration
@ConditionalOnClass(OpenAiApi.class)
@EnableConfigurationProperties({ OpenAiConnectionProperties.class, OpenAiChatProperties.class,
		OpenAiEmbeddingProperties.class })
@ImportRuntimeHints(NativeHints.class)
public class OpenAiAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public OpenAiChatClient openAiChatClient(OpenAiConnectionProperties commonProperties,
			OpenAiChatProperties chatProperties) {

		String apiKey = StringUtils.hasText(chatProperties.getApiKey()) ? chatProperties.getApiKey()
				: commonProperties.getApiKey();

		String baseUrl = StringUtils.hasText(chatProperties.getBaseUrl()) ? chatProperties.getBaseUrl()
				: commonProperties.getBaseUrl();

		Assert.hasText(apiKey, "OpenAI API key must be set");
		Assert.hasText(baseUrl, "OpenAI base URL must be set");

		var openAiApi = new OpenAiApi(baseUrl, apiKey, RestClient.builder());

		OpenAiChatClient openAiChatClient = new OpenAiChatClient(openAiApi);
		openAiChatClient.setTemperature(chatProperties.getTemperature());
		openAiChatClient.setModel(chatProperties.getModel());

		return openAiChatClient;
	}

	@Bean
	@ConditionalOnMissingBean
	public EmbeddingClient openAiEmbeddingClient(OpenAiConnectionProperties commonProperties,
			OpenAiEmbeddingProperties embeddingProperties) {

		String apiKey = StringUtils.hasText(embeddingProperties.getApiKey()) ? embeddingProperties.getApiKey()
				: commonProperties.getApiKey();
		String baseUrl = StringUtils.hasText(embeddingProperties.getBaseUrl()) ? embeddingProperties.getBaseUrl()
				: commonProperties.getBaseUrl();

		Assert.hasText(apiKey, "OpenAI API key must be set");
		Assert.hasText(baseUrl, "OpenAI base URL must be set");

		var openAiApi = new OpenAiApi(baseUrl, apiKey, RestClient.builder());

		return new OpenAiEmbeddingClient(openAiApi, embeddingProperties.getModel());
	}

}
