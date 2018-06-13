# GADataStoring
To store google analytics data and reporting.

Used examples from [Hello Analytics Sample](https://github.com/google/google-api-java-client-samples/blob/master/analytics-cmdline-sample/src/main/java/com/google/api/services/samples/analytics/cmdline/HelloAnalyticsApiSample.java)

# Data Storing
## Configuration
* Update ./src/main/resources/client_secrets.json with your client_id and client_secret.
* Update ./src/main/java/configuration/Config.java
* APPLICATION_NAME and TABLE_ID.
* Check mongo db name and collection names.
* Check Elasticsearch index and mapping.
* For scheduling try with /src/main/java/scheduler/ScheduledRetriever.java

## Elasticsearch (6.2) mapping details 
```
PUT ga_sessions
{
  "mappings": {
    "session": {
      "properties": {
        "ga:sessions": {
          "type": "integer"
        },"ga:timestamp":{
           "type":"date",
           "format":"YYYY-MM-dd HH:mm:ss"
        }
      }
    }
  }
},
 
PUT ga_locations
{
  "mappings": {
    "location": {
      "properties": {
      "ga:continent": {
          "type": "keyword"
        },
        "ga:country": {
          "type": "keyword"
        },
        "ga:city": {
          "type": "keyword"
        },
        "ga:sessions": {
          "type": "integer"
        },"ga:date":{
           "type":"date",
           "format":"YYYY-MM-dd"
        }
      }
    }
  }
}
PUT ga_devices
{
  "mappings": {
    "device": {
      "properties": {
   "ga:mobileDeviceInfo":{
     "type": "keyword"
   },
      "ga:continent": {
          "type": "keyword"
        },
        "ga:country": {
          "type": "keyword"
        },
        "ga:city": {
          "type": "keyword"
        },
        "ga:sessions": {
          "type": "integer"
        },"ga:date":{
           "type":"date",
           "format":"YYYY-MM-dd"
        }
      }
    }
  }
}
   
PUT ga_pages
{
 "mappings" :{
    "page": {
      "properties": {
        "ga:pagePath": {
          "type": "keyword"
        },
      "ga:continent": {
          "type": "keyword"
        },
        "ga:country": {
          "type": "keyword"
        },
        "ga:city": {
          "type": "keyword"
        },
        "ga:pageviews": {
          "type": "integer"
        },"ga:date":{
           "type":"date",
           "format":"YYYY-MM-dd"
        }
      }
    }}
}

PUT ga_geo_locations
{
  "mappings": {
    "geo_location": {
      "properties": {
        "location": {
          "type": "geo_point",
          "ignore_malformed": true,
		  "geohash_prefix":     true,
		   "geohash_precision":  "1km"
        },
        "ga:sessions": {
          "type": "integer"
        },"ga:timestamp":{
           "type":"date",
           "format":"YYYY-MM-dd HH:mm:ss"
        }
      }
    }
  }
}

```
# Reporting
Try with Kibana

# License
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
