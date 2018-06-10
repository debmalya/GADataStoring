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

```
# Reporting
