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
package data;

import java.io.IOException;
import java.util.Calendar;

import org.apache.log4j.Logger;

import com.google.api.services.analytics.Analytics;
import com.google.api.services.analytics.model.GaData;
import com.google.gson.JsonObject;

import configuration.Config;
import dao.ElasticSearchDAO;
import util.GAUtility;

public class DataLoading {

	private static final Logger LOGGER = Logger.getLogger(DataLoading.class);

    private static final ElasticSearchDAO elasticSearchDao = Config.getElasticSearchDao();

    public static void main(String[] args) {
        Calendar today = Config.utcCal;
        try {
            Analytics analytics = Config.initializeAnalytics();
            // Last 30 days
            // TODO: this can be parameterized
            for (int i = 1; i < 31; i++) {
                today.add(Calendar.DATE, -1);
                try {
                    String fetchingDate = Config.sdf.format(today.getTime());
                    
                    // For location
                    GaData sessionData = execute(analytics, Config.TABLE_ID, fetchingDate, fetchingDate, "ga:sessions", "ga:continent,ga:country,ga:city");
                    JsonObject jsonObject = GAUtility.createJsonObjectFromGAData(sessionData, fetchingDate,today);
                    elasticSearchDao.store(Config.ELASTIC_LOCATIONS, jsonObject, fetchingDate);
                    
                    
                    // For Devices
                    sessionData = execute(analytics, Config.TABLE_ID, fetchingDate, fetchingDate, "ga:sessions", "ga:mobileDeviceInfo,ga:continent,ga:country,ga:city");
                    jsonObject = GAUtility.createJsonObjectFromGAData(sessionData, fetchingDate,today);
                    elasticSearchDao.store(Config.ELASTIC_DEVICES, jsonObject, fetchingDate);
                    
                    // For pages
                    sessionData = execute(analytics, Config.TABLE_ID, fetchingDate, fetchingDate, "ga:pageviews", "ga:pagePath,ga:continent,ga:country,ga:city");
                    jsonObject = GAUtility.createJsonObjectFromGAData(sessionData, fetchingDate,today);
                    elasticSearchDao.store(Config.ELASTIC_PAGES, jsonObject, fetchingDate);
                } catch (Throwable th) {
                    th.printStackTrace();
                    LOGGER.error(th.getMessage(), th);
                }
            }
        }catch(Throwable th){
            th.printStackTrace();
            LOGGER.error(th.getMessage(),th);
        }

        System.out.println("Thanks ... Bye");
        System.exit(0);
    }


    /**
     * @param analytics  - Google Analytics Object.
     * @param tableId    - table id from google API console.
     * @param startDate  - start date to collect analytics data.
     * @param endDate    - end date to collect analytics data.
     * @param metrics    - metrics to be collected (e.g. ga:sessions )
     * @param dimensions - dimensions (e.g.
     * @return  Google Analytics data.
     * @throws IOException
     */
    public static GaData execute(final Analytics analytics, final String tableId, final String startDate, final String endDate, final String metrics, String dimensions) throws IOException {
        return analytics.data().ga().get(tableId, // Table Id.
                startDate, // Start date.
                endDate, // End date.
                metrics) // Metrics.
                .setDimensions(dimensions)
//                .setSort("-ga:visits,ga:source")
//                .setFilters("ga:medium==organic")
                .setMaxResults(10000)
                .execute();
    }

}
