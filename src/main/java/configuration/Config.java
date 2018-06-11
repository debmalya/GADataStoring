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

import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.TimeZone;

import javax.jdo.Constants;

import org.apache.log4j.Logger;
import org.bson.Document;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.analytics.Analytics;
import com.google.api.services.analytics.AnalyticsScopes;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import dao.ElasticHost;
import dao.ElasticSearchDAO;

public class Config {
	
	/**
     * Global instance of the HTTP transport.
     */
    public static HttpTransport HTTP_TRANSPORT;

    /**
     * This stores application name.
     */
    public static final String APPLICATION_NAME = "";

    /**
     * Used to identify from which reporting profile to retrieve data. Format is ga:xxx where xxx is
     * your profile ID.
     */
    public static final String TABLE_ID = "";

    /**
     * Directory to store user credentials.
     */
    public static final java.io.File DATA_STORE_DIR =
            new java.io.File(System.getProperty("user.home"), "store/analytics_sample");
    public final static MongoClient client = new MongoClient();
    public final static MongoDatabase db = client.getDatabase("GA");
    public final static MongoCollection<Document> sessions = db.getCollection("Sessions");
    public final static MongoCollection<Document> dailySessions = db.getCollection("DailySessions");
    public final static MongoCollection<Document> typeOfDevices = db.getCollection("TypeOfDevices");
    public final static MongoCollection<Document> pageViews = db.getCollection("PageViews");
    /**
     * UTC time zone calendar
     */
    public final static Calendar utcCal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
    public static final SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd");
    /**
     * Global instance of the JSON factory.
     */
    public static JacksonFactory JSON_FACTORY = new JacksonFactory();
    /**
     * Global instance of the {@link DataStoreFactory}. The best practice is to make it a single
     * globally shared instance across your application.
     */
    private static FileDataStoreFactory DATA_STORE_FACTORY;

    /**
     * To log error and debug messages
     */
    private static final Logger LOGGER = Logger.getLogger(Constants.class);

    /**
     * Elastic search index name for storing ga:session (metrics) ga:continent,ga:country,ga:city (dimension)
     */
    public static final String ELASTIC_LOCATIONS = "/ga_locations/location/";

    /**
     * Elastic search index name for storing ga:session (metrics) timestamp
     */
    public static final String ELASTIC_SESSIONS = "/ga_sessions/session/";

    /**
     * Elastic search index name for storing ga:session (metrics) timestamp
     */
    public static final String ELASTIC_DEVICES = "/ga_devices/device/";

    /**
     * Elastic search index name for storing ga:session (metrics) timestamp
     */
    public static final String ELASTIC_PAGES = "/ga_pages/page/";

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (Throwable th) {
            th.printStackTrace();
            LOGGER.error(th.getMessage(), th);
        }
    }

    private Config(){

    }
    /**
     * Performs all necessary setup steps for running requests against the API.
     *
     * @return an initialized Analytics service object.
     * @throws Exception if an issue occurs with OAuth2Native authorize.
     */
    public static Analytics initializeAnalytics() throws Exception {
        // Authorization.
        Credential credential = authorize();

        // Set up and return Google Analytics API client.
        return new Analytics.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(
                APPLICATION_NAME).build();
    }

    /**
     * Authorizes the installed application to access user's protected data.
     */
    private static Credential authorize() throws Exception {
        // load client secrets
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
                JSON_FACTORY, new InputStreamReader(
                        Config.class.getResourceAsStream("/client_secrets.json")));
        if (clientSecrets.getDetails().getClientId().startsWith("Enter")
                || clientSecrets.getDetails().getClientSecret().startsWith("Enter ")) {
            System.out.println(
                    "Enter Client ID and Secret from https://code.google.com/apis/console/?api=analytics "
                            + "into analytics-cmdline-sample/src/main/resources/client_secrets.json");
            System.exit(1);
        }
        // set up authorization code flow
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets,
                Collections.singleton(AnalyticsScopes.ANALYTICS_READONLY)).setDataStoreFactory(
                DATA_STORE_FACTORY).build();
        // authorize
        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");

    }

    /**
     *
     * @return Elastic search dao instance.
     */
    public static ElasticSearchDAO getElasticSearchDao(){
        ElasticSearchDAO searchDAO = null;
        try {
             searchDAO = new ElasticSearchDAO(new ElasticHost[]{new ElasticHost("localhost", 9200)});
        } catch (Throwable th) {
            th.printStackTrace();
            LOGGER.error(th.getMessage(),th);
        }
        return searchDAO;
    }
   
    /**
     * To cleanup resources
     */
	public static void cleanUp(){
		if (client != null){
			client.close();
		}
	}
}
