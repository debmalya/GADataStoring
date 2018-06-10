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

import com.google.api.services.analytics.model.GaData;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class GAUtility {
	private GAUtility(){

    }

    /**
     *
     * @param gaData - Google Analytics Data
     * @param key - index key (usually a date in YYYY-MM-dd format e.g. 2018-06-10 )
     * @param today
     * @return JsonObject to be stored in Elasticsearch or MongoDB.
     */
    public static JsonObject createJsonObjectFromGAData(GaData gaData, String key, Calendar today){
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
                dateJson.add("ga:date",new JsonPrimitive(key));
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
}
