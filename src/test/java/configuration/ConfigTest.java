/**
 * Copyright 2018-2019 Debmalya Jash
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package configuration;

import org.junit.Assert;
import org.junit.Test;



public class ConfigTest {

	@Test
	public void testInitializeAnalytics() {
		try {
			Assert.assertNotNull(Config.initializeAnalytics());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.assertFalse("Check your configuration client_secrets.json",true);
		}
	}

	@Test
	public void testGetElasticSearchDao() {
		Assert.assertNotNull("Check your configuration, whether Elasticsearch is running or not",Config.getElasticSearchDao());
	}

}
