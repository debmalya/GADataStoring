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
package scheduler;

import org.apache.log4j.Logger;

import com.google.api.services.analytics.Analytics;
import com.google.api.services.analytics.model.GaData;

import configuration.Config;
import util.GAUtility;

public class LatitudeLongitudeRetriever implements Runnable {
	
	private static Logger LOGGER  = Logger.getLogger(LatitudeLongitudeRetriever.class);

	@Override
	public void run() {
		try {
			Analytics googleAnalytics = Config.initializeAnalytics();
			GaData gaData = GAUtility.execute(googleAnalytics, Config.TABLE_ID, "today", "today", "ga:sessions", "ga:latitude,ga:longitude", null);
			GAUtility.storeGaDataInCSV(gaData, "LatLong.csv");
		} catch (Throwable th) {
			LOGGER.error(th.getMessage(),th);
		}

	}

}
