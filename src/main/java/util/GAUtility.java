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
package util;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.bson.Document;

import com.google.api.services.analytics.Analytics;
import com.google.api.services.analytics.model.GaData;
import com.google.api.services.analytics.model.GaData.ColumnHeaders;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mongodb.client.MongoCollection;
import com.opencsv.CSVWriter;

import dao.ElasticSearchDAO;

public class GAUtility {
	/**
	 * To write error , debug message
	 */
	private static final Logger LOGGER = Logger.getLogger(GAUtility.class);

	private GAUtility() {

	}

	/**
	 *
	 * @param gaData
	 *            - Google Analytics Data
	 * @param key
	 *            - index key (usually a date in YYYY-MM-dd format e.g.
	 *            2018-06-10 )
	 * @param today
	 * @return JsonObject to be stored in Elasticsearch or MongoDB.
	 */
	public static JsonObject createJsonObjectFromGAData(GaData gaData, String key, Calendar today) {
		JsonObject jsonObject = new JsonObject();
		if (gaData.getTotalResults() > 0) {
			System.out.println("Data Table:");

			// Print the column names.
			List<String> columnHeaders = new ArrayList<>();

			for (GaData.ColumnHeaders header : gaData.getColumnHeaders()) {
				System.out.format("%-32s", header.getName());
				columnHeaders.add(header.getName());
			}

			System.out.println();

			// Print the rows of data.
			JsonArray allRows = new JsonArray();
			for (List<String> rowValues : gaData.getRows()) {
				List<String> dataRows = new ArrayList<>();
				JsonArray eachRow = new JsonArray();
				int colIndex = 0;

				for (String value : rowValues) {
					System.out.format("%-32s", value);
					dataRows.add(value);
					JsonObject eachElement = new JsonObject();
					eachElement.add(columnHeaders.get(colIndex), new JsonPrimitive(value));
					eachRow.add(eachElement);
					colIndex++;
				}

				JsonObject dateJson = new JsonObject();
				dateJson.add("ga:date", new JsonPrimitive(key));
				eachRow.add(dateJson);
				allRows.add(eachRow);
				System.out.println();
			}

			jsonObject.add(key, allRows);
		} else {
			System.out.println("No data");
		}

		return jsonObject;
	}

	/**
	 * @param analytics
	 *            - Google Analytics Object.
	 * @param tableId
	 *            - table id from google API console.
	 * @param startDate
	 *            - start date to collect analytics data.
	 * @param endDate
	 *            - end date to collect analytics data.
	 * @param metrics
	 *            - metrics to be collected (e.g. ga:sessions )
	 * @param dimensions
	 *            - dimensions (e.g.
	 * @return Google Analytics data.
	 * @throws IOException
	 */
	public static GaData execute(final Analytics analytics, final String tableId, final String startDate,
			final String endDate, final String metrics, final String dimensions, final String sortExpression)
			throws IOException {
		return analytics.data().ga()
				.get(tableId, // Table Id.
						startDate, // Start date.
						endDate, // End date.
						metrics) // Metrics.
				.setDimensions(dimensions)
				// .setSort(sortExpression) // "-ga:visits,ga:source"
				// .setFilters("ga:medium==organic")
				.setMaxResults(10000).execute();
	}

	/**
	 * 
	 * @param gaData
	 * @param collectionName
	 */
	public static void storeGaDataInCSV(final GaData gaData, final String fileName) {
		if (gaData.getTotalResults() > 0) {

			try (CSVWriter writer = new CSVWriter(new PrintWriter(fileName))) {
				// Header
				List<String> header = new ArrayList<>();
				for (ColumnHeaders eachColumnHeaders : gaData.getColumnHeaders()) {
					header.add(eachColumnHeaders.getName());
				}
				writer.writeNext(header.toArray(new String[0]));

				// Rows
				for (List<String> rowValues : gaData.getRows()) {
					List<String> dataRows = new ArrayList<>();
					
					
					for (String value : rowValues) {
						dataRows.add(value);
					}
					writer.writeNext(dataRows.toArray(new String[0]));
					
				}
				writer.flushQuietly();
			} catch (IOException ioe) {
				LOGGER.error(ioe.getMessage(), ioe);
			}
		} else {
			// No Data
		}
	}

	
	public static void createJsonObjectFromGAData(GaData gaData, String fetchingDate, Calendar today, ElasticSearchDAO elasticSearchDao,final String collectionName) throws IOException {
		   
	    JsonArray allRows = new JsonArray();
	    if (gaData.getTotalResults() > 0) {
	        System.out.println("Data Table:");

	        // Print the column names.
	        List<String> columnHeaders = new ArrayList<>();

	        for (GaData.ColumnHeaders header : gaData.getColumnHeaders()) {
	            System.out.format("%-32s", header.getName());
	            columnHeaders.add(header.getName());
	        }

	        System.out.println();

	        // Print the rows of data.

	        for (List<String> rowValues : gaData.getRows()) {
	            List<String> dataRows = new ArrayList<>();
	            JsonObject eachRow = new JsonObject();
	            int colIndex = 0;

	            for (String value : rowValues) {
	                System.out.format("%-32s", value);
	                dataRows.add(value);
	                JsonObject eachElement = new JsonObject();
	                String columnName = columnHeaders.get(colIndex);

	                eachRow.add(columnHeaders.get(colIndex), new JsonPrimitive(value));
	                colIndex++;
	            }

	            JsonObject dateJson = new JsonObject();
	            eachRow.add("ga:date",new JsonPrimitive(fetchingDate));

	            // add eachRow to Elasticsearch
	            elasticSearchDao.store(collectionName,eachRow);
	            allRows.add(eachRow);
	            System.out.println();
	        }
	    } else {
	        System.out.println("No data");
	    }
	}

	/**
	 * 
	 * @param gaData
	 * @param collectionName
	 */
	public static void storeGaDataInMongo(GaData gaData, MongoCollection<Document> collectionName) {
		if (gaData.getTotalResults() > 0) {

		} else {
			// No Data
		}
	}
}
