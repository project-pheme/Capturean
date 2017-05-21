# Capture REST services

This document describes Capture REST API. The API comsist of various REST services that allows to work with Data channels and perform basic operations, such as opening new data channel, monitoring data channels, changing & updating data channel properties or removing them.

Apart from the API documentation, this document also describes how to work with Capture and data channels.

Table of Contents
=================

   * [Capture REST services](#capture-rest-services)
   * [Table of Contents](#table-of-contents)
      * [Working with historical tweets](#working-with-historical-tweets)
         * [Limiting the number of historical tweets](#limiting-the-number-of-historical-tweets)
      * [Working with live tweets](#working-with-live-tweets)
         * [Using the Twitter Stream API](#using-the-twitter-stream-api)
         * [Using the Chronological Order mode](#using-the-chronological-order-mode)
      * [Query preview service](#query-preview-service)
         * [GET /search/tweets](#get-searchtweets)
         * [Note on the output format](#note-on-the-output-format)
      * [Datachannel Query services](#datachannel-query-services)
         * [GET /datachannel](#get-datachannel)
         * [POST /datachannel](#post-datachannel)
         * [GET /datachannel/{id}](#get-datachannelid)
         * [PUT /datachannel/{id}](#put-datachannelid)
         * [DELETE /datachannel/{id}](#delete-datachannelid)



## Working with historical tweets

When creating a new datachannel, by default it will use search API to retrieve all historical tweets: starting from the moment the data channel is created up to the oldest tweet available through the Twitter API (typically up to 7-10 days).

An example for creating regular data channel to retrieve historical tweets:
```json
{
  "dataSources": [
    {
      "twitter": {
        "type": "Twitter",
        "keywords": "madrid lang:es",
        "fromLastTweetId": false,
        "chronologicalOrder": false,
      }
    }
  ],
  "name": "Madrid historical tweets test 1",
  "type": "search",
  "description": "",
  "startCaptureDate": "2017-01-27 12:48:09.000",
  "endCaptureDate": "2017-02-01 17:55:09.000"
}
```

Creating (opening) a new data channel using the Capture API is by sending a POST message to the service endpoint, e.g.:

```bash
curl -X POST -d @new-datachannel.json -H "Content-Type: application/json"  http://gatezkt1:8080/CaptureREST/rest/datachannel/
```
where new-datachannel.json is the file containing the JSON description of the new data channel.

### Limiting the number of historical tweets

Getting the whole history can take some time, so there is an option to limit the amount of historical tweets using "historicalLimit" field in the "dataSources" part of the data channel. For example:

```json
{
  "dataSources": [
    {
      "twitter": {
        "type": "Twitter",
        "keywords": "madrid lang:es",
        "fromLastTweetId": false,
        "chronologicalOrder": false,
        "historicalLimit": 200
      }
    }
  ],
  "name": "Madrid historical tweets test 1",
  "type": "search",
  "description": "",
  "startCaptureDate": "2017-01-27 12:48:09.000",
  "endCaptureDate": "2017-02-01 17:55:09.000"
}
```

This way a new datachannel will start in "historical mode". After the amount of historical tweets is acquired (as specified by `historicalLimit`) and the data channel haven't expired yet, it will automatically switch into "chronological order" mode, acquiring only new tweets seen since the beginning of the search. From now on, the behaviour is consistent with chapter [Using the "chronological order" mode](#Using-the-"chronological-order"-mode) .
The data channel will stop sending new data after reaching the "endCaptureDate" instantly.

## Working with live tweets

When the goal is to work in real time and monitor some developing events, the best it is to work with live streams.

There are two ways of working with live tweets:
- using the Twitter Stream API
- using the "chronologicalOrder" mode.

### Using the Twitter Stream API

Using the Twitter Stream API consis of opening a data channel of type "Stream". Ther can be only 1 runnning stream at the same time, which is the limit of the Twitter API. An example of such datachannel is given below.

```json
{
  "dataSources": [
    {
      "twitter": {
        "type": "Twitter",
        "keywords": "madrid;lang:es",
      }
    }
  ],
  "name": "Madrid historical tweets test 1",
  "type": "stream",
  "description": "",
  "startCaptureDate": "2017-01-27 12:48:09.000",
  "endCaptureDate": "2017-02-01 17:55:09.000"
```

The important part is the "type": "stream" and the syntax of the query ("keywords" field). The syntax is the following: `<keywords> ; <language> ; <geo> ; <follow>`. Details on the syntax for the `<keywords>` part can be found in: https://dev.twitter.com/streaming/overview/request-parameters#track

Please note that there can be only one data source in the stream datachannel.

### Using the Chronological Order mode

The "Chronological order" mode is using Twitter Search API to simulate stream, by constantly doing polling and retrieving only latest (previously unseen) tweets. It has one important advantage over the true stream mode: there can be multiple datachannels in "chronological order" mode, while there can be only one in "stream" mode.

Chronological order is set on the data source level and it consists of setting the "chronologicalOrder" property to true. An example is given below:
```json
{
  "dataSources": [
    {
      "twitter": {
        "type": "Twitter",
        "keywords": "madrid;lang:es",
        "chronologicalOrder": true,
      }
    }
  ],
  "name": "Madrid historical tweets test 1",
  "type": "search",
  "description": "",
  "startCaptureDate": "2017-01-27 12:48:09.000",
  "endCaptureDate": "2017-02-01 17:55:09.000"
```

Note that the "chronological order" datachannel is of type "search".


## Query preview service

Resources:

- [/search/tweets](#/search/tweets)
	- [GET /search/tweets](#get-/search/tweets)

### GET /search/tweets

This method provide a quick query preview service, to check query keywords before running an actual datachannel. It is used to fine tune twitter queries to avoid noisy keywords or find more precise queries.

Endpoint:

```
http://gatezkt1:8080/CaptureREST/rest/search/tweets
```

Methods:

* GET

Media types formats:

*  application/json
*  application/xml

Parameters:

| Name        | Type   | Description  |
|-----------  |--------|--------------|
| keywords    | string | Keywords Twitter search keywords, as accepted by Twitter API. See https://dev.twitter.com/rest/reference/get/search/tweets for query grammar. Keywords should be URL encoded. |
| mode        | string | One of the following modes: live, most_popular, mixed. Default is "live" (latest).  |
| max_results | number | Specify max number of results. Default: 500  |


Example invocation:

```
curl -X GET -H "Accept: application/json" 'http://gatezkt1:8080/CaptureREST/rest/search/tweets?mode=live&keywords=iran%20cash' 

```

Example reply (JSON):
```
> GET /CaptureREST/rest/search/tweets?mode=live&keywords=iran%20cash&max_results=100 HTTP/1.1
> User-Agent: curl/7.35.0
> Host: gatezkt1:8080
> Accept: application/json
> 
< HTTP/1.1 200 OK
< Content-Type: application/json
< Transfer-Encoding: chunked
< Server: Jetty(9.2.2.v20140723)
```

```json
[
  {
    "sentimentRaw": null,
    "annotationPolitical": [],
    "annotationGeo": [],
    "dangerousness": null,
    "stress": null,
    "sentiment": null,
    "rawJson": "{\"metadata\":{\"result_type\":\"recent\",\"iso_language_code\":\"en\"},\"quoted_status\":{\"metadata\":{\"result_type\":\"recent\",\"iso_language_code\":\"en\"},\"in_reply_to_status_id_str\":null,\"in_reply_to_status_id\":null,\"created_at\":\"Wed Oct 19 13:53:38 +0000 2016\",\"in_reply_to_user_id_str\":null,\"source\":\"<a href=\\\"http://twitter.com/download/iphone\\\" rel=\\\"nofollow\\\">Twitter for iPhone<\\/a>\",\"retweet_count\":10,\"retweeted\":false,\"geo\":null,\"in_reply_to_screen_name\":null,\"is_quote_status\":false,\"id_str\":\"788739888006909953\",\"in_reply_to_user_id\":null,\"favorite_count\":4,\"id\":788739888006909953,\"text\":\"American father and son sentenced to 10 years in Iranian prison https://t.co/aPrmwmGnY8\",\"place\":null,\"lang\":\"en\",\"favorited\":false,\"possibly_sensitive\":false,\"coordinates\":null,\"truncated\":false,\"entities\":{\"urls\":[{\"display_url\":\"cnn.com/2016/10/18/wor\\u2026\",\"indices\":[64,87],\"expanded_url\":\"http://www.cnn.com/2016/10/18/world/iranian-americans-sentenced-10-years/index.html\",\"url\":\"https://t.co/aPrmwmGnY8\"}],\"hashtags\":[],\"user_mentions\":[],\"symbols\":[]},\"contributors\":null,\"user\":{\"utc_offset\":-14400,\"friends_count\":5037,\"profile_image_url_https\":\"https://pbs.twimg.com/profile_images/416044148425502720/FRKCS5pH_normal.jpeg\",\"listed_count\":13369,\"profile_background_image_url\":\"http://pbs.twimg.com/profile_background_images/743633139/bae4b64f714195d0c2d2dff162ac596e.jpeg\",\"default_profile_image\":false,\"favourites_count\":6122,\"description\":\"CNN Anchor and Chief Washington Correspondent. Dissecting my tweets with Talmudic meticulousness will result in wrong conclusions. RTs do not = endorsement.\",\"created_at\":\"Fri Apr 25 17:23:28 +0000 2008\",\"is_translator\":false,\"profile_background_image_url_https\":\"https://pbs.twimg.com/profile_background_images/743633139/bae4b64f714195d0c2d2dff162ac596e.jpeg\",\"protected\":false,\"screen_name\":\"jaketapper\",\"id_str\":\"14529929\",\"profile_link_color\":\"088253\",\"is_translation_enabled\":false,\"translator_type\":\"none\",\"id\":14529929,\"geo_enabled\":true,\"profile_background_color\":\"EDECE9\",\"lang\":\"en\",\"has_extended_profile\":false,\"profile_sidebar_border_color\":\"FFFFFF\",\"profile_text_color\":\"634047\",\"verified\":true,\"profile_image_url\":\"http://pbs.twimg.com/profile_images/416044148425502720/FRKCS5pH_normal.jpeg\",\"time_zone\":\"Eastern Time (US & Canada)\",\"url\":\"http://t.co/wwbPcS72qd\",\"contributors_enabled\":false,\"profile_background_tile\":false,\"profile_banner_url\":\"https://pbs.twimg.com/profile_banners/14529929/1353593155\",\"entities\":{\"description\":{\"urls\":[]},\"url\":{\"urls\":[{\"display_url\":\"facebook.com/OutpostBook\",\"indices\":[0,22],\"expanded_url\":\"http://www.facebook.com/OutpostBook\",\"url\":\"http://t.co/wwbPcS72qd\"}]}},\"statuses_count\":128746,\"follow_request_sent\":null,\"followers_count\":584330,\"profile_use_background_image\":true,\"default_profile\":false,\"following\":null,\"name\":\"Jake Tapper\",\"location\":\"\",\"profile_sidebar_fill_color\":\"E3E2DE\",\"notifications\":null}},\"in_reply_to_status_id_str\":null,\"in_reply_to_status_id\":null,\"created_at\":\"Wed Oct 19 14:12:02 +0000 2016\",\"in_reply_to_user_id_str\":null,\"source\":\"<a href=\\\"http://twitter.com/download/android\\\" rel=\\\"nofollow\\\">Twitter for Android<\\/a>\",\"quoted_status_id\":788739888006909953,\"retweet_count\":0,\"retweeted\":false,\"geo\":null,\"in_reply_to_screen_name\":null,\"is_quote_status\":true,\"id_str\":\"788744519965630465\",\"in_reply_to_user_id\":null,\"favorite_count\":0,\"id\":788744519965630465,\"text\":\"This is horrible. Further proof that Iran is a barbaric regime that Obozo gave 1.2 billion in cash to. https://t.co/t0WCcchSuc\",\"place\":null,\"lang\":\"en\",\"favorited\":false,\"possibly_sensitive\":false,\"coordinates\":null,\"truncated\":false,\"entities\":{\"urls\":[{\"display_url\":\"twitter.com/jaketapper/sta\\u2026\",\"indices\":[103,126],\"expanded_url\":\"https://twitter.com/jaketapper/status/788739888006909953\",\"url\":\"https://t.co/t0WCcchSuc\"}],\"hashtags\":[],\"user_mentions\":[],\"symbols\":[]},\"quoted_status_id_str\":\"788739888006909953\",\"contributors\":null,\"user\":{\"utc_offset\":null,\"friends_count\":108,\"profile_image_url_https\":\"https://pbs.twimg.com/profile_images/447064846467473408/xtlL0jlp_normal.jpeg\",\"listed_count\":1,\"profile_background_image_url\":\"http://abs.twimg.com/images/themes/theme1/bg.png\",\"default_profile_image\":false,\"favourites_count\":339,\"description\":\"\",\"created_at\":\"Thu May 21 05:19:58 +0000 2009\",\"is_translator\":false,\"profile_background_image_url_https\":\"https://abs.twimg.com/images/themes/theme1/bg.png\",\"protected\":false,\"screen_name\":\"EmmBliss\",\"id_str\":\"41534914\",\"profile_link_color\":\"0084B4\",\"is_translation_enabled\":false,\"translator_type\":\"none\",\"id\":41534914,\"geo_enabled\":false,\"profile_background_color\":\"C0DEED\",\"lang\":\"en\",\"has_extended_profile\":true,\"profile_sidebar_border_color\":\"C0DEED\",\"profile_text_color\":\"333333\",\"verified\":false,\"profile_image_url\":\"http://pbs.twimg.com/profile_images/447064846467473408/xtlL0jlp_normal.jpeg\",\"time_zone\":null,\"url\":null,\"contributors_enabled\":false,\"profile_background_tile\":false,\"entities\":{\"description\":{\"urls\":[]}},\"statuses_count\":330,\"follow_request_sent\":null,\"followers_count\":26,\"profile_use_background_image\":true,\"default_profile\":true,\"following\":null,\"name\":\"Edie Murgia\",\"location\":\"\",\"profile_sidebar_fill_color\":\"DDEEF6\",\"notifications\":null}}",
    "latLong": null,
    "longitude": null,
    "latitude": null,
    "userFollowers": 26,
    "userFollowes": 108,
    "place": null,
    "sourceUrls": "https://t.co/t0WCcchSuc",
    "hashTags": "",
    "source": "<a href=\"http://twitter.com/download/android\" rel=\"nofollow\">Twitter for Android</a>",
    "userID": "41534914",
    "lang": "en",
    "text": "This is horrible. Further proof that Iran is a barbaric regime that Obozo gave 1.2 billion in cash to. https://t.co/t0WCcchSuc",
    "dataID": "Tweet788744519965630465",
    "dataSources": [],
    "tweetID": "788744519965630465",
    "dataPools": [],
    "userScreenName": "EmmBliss",
    "userName": null,
    "userDescription": "",
    "createdAt": 1476886322000,
    "inReplyToId": "-1",
    "originalTweetId": null,
    "retweetCount": 0,
    "favouriteCount": 0
  }
]

```

### Note on the output format
On the top level we provide most important Twitter fields (such as text, date, userScreenName, tweet id). In case when the raw Twitter reply is necessary, we can provide a raw JSON as retrieved from the Twitter API. Currently we simply encode it as a string, in a "rawJSON" top level field. In order to make use of it it is necessary to parse this string as JSON object.


## Datachannel Query services

Resources:

- [/datachannel](#/datachannel)
	- [GET /datachannel](#get-/datachannel)
	- [POST /datachannel](#post-/datachannel)

- [/datachannel/{id}](#/datachannel/{id})
	- [GET /datachannel/{id}](#get-/datachannel/{id})
	- [PUT /datachannel/{id}](#put-/datachannel/{id})
	- [DELETE /datachannel/{id}](#delete-/datachannel/{id})

###GET /datachannel

This method lists all created DataChannels.

Endpoint:

```
http://gatezkt1:8080/CaptureREST/rest/datachannel
```


Media types formats:

*  application/json
*  application/xml

Parameters:



Example invocation:

```bash
curl -X GET -H "Accept: application/json" 'http://gatezkt1:8080/CaptureREST/rest/datachannel/'

```


Example response:

```
> GET /CaptureREST/rest/datachannel/ HTTP/1.1
> User-Agent: curl/7.35.0
> Host: gatezkt1:8080
> Accept: application/json
> 
< HTTP/1.1 200 OK
< Content-Type: application/json
< Transfer-Encoding: chunked
< Server: Jetty(9.2.7.v20150116)

```

```json
{
  "dataChannel": [
    {
      "dataChannelState": null,
      "maxHistoricalTweetLimit": null,
      "tweetRate": null,
      "status": "active",
      "endCaptureDate": "2016-09-24 12:08:19.000",
      "startCaptureDate": "2016-06-24 12:08:19.000",
      "dataSources": [
        {
          "twitter": {
            "chronologicalOrder": true,
            "fromLastTweetId": true,
            "lastTweetId": 779640130860638200,
            "keywords": "#brexit OR #bremain OR #voteleave OR #remain OR #brexitornot",
            "type": "Twitter",
            "sourceID": "fb853c3c-02b7-44c4-aac3-bb887674c2f0"
          }
        }
      ],
      "totalTweetCount": null,
      "channelID": "6c9fcb94",
      "name": "Brexit Referendum",
      "type": "search",
      "description": "Brexit Referendum",
      "creationDate": "2016-06-24 10:10:20.802",
      "updateDate": "2016-09-24 11:15:02.289"
    },
    {
      "dataChannelState": null,
      "maxHistoricalTweetLimit": null,
      "tweetRate": null,
      "status": "active",
      "endCaptureDate": "2017-09-08 03:19:56.000",
      "startCaptureDate": "2016-08-31 21:31:10.000",
      "dataSources": [
        {
          "twitter": {
            "chronologicalOrder": true,
            "fromLastTweetId": true,
            "lastTweetId": 788788313079574500,
            "keywords": "LRens",
            "type": "Twitter",
            "sourceID": "9a75d3ce-130a-44f1-9897-0fc80ec5ea45"
          }
        },
        {
          "twitter": {
            "chronologicalOrder": true,
            "fromLastTweetId": true,
            "lastTweetId": 788777530987802600,
            "keywords": "#ndg",
            "type": "Twitter",
            "sourceID": "42df10f3-f8e9-448f-a7c7-eb8a0214be72"
          }
        },
        {
          "twitter": {
            "chronologicalOrder": true,
            "fromLastTweetId": true,
            "lastTweetId": 787588837715374100,
            "keywords": "#nachrichtendienstgesetz",
            "type": "Twitter",
            "sourceID": "627d2d42-75ab-42dd-b719-b26747d36dc3"
          }
        }
      ],
      "totalTweetCount": null,
      "channelID": "cb6e4a1d",
      "name": "Intelligence reform",
      "type": "search",
      "description": "Intelligence reform",
      "creationDate": "2016-09-07 21:31:10.357",
      "updateDate": "2016-10-19 17:07:58.555"
    }
  ]
}

```

### POST /datachannel

This method provides means for creating new Data Channels.

Endpoint:

```
http://gatezkt1:8080/CaptureREST/rest/datachannel
```

Accepts Media types formats:

*  application/json
*  application/xml

Parameters:

The method accepts datachannel definition as POST payload. The Datachannel format is the same as retrieved from the [GET method](#get-/datachannel/{id}):

```json
{
    "dataSources": [
        {
            "twitter": {
                "keywords": "carbonero casillas",
                "type": "Twitter"
            }
        }
    ],
    "startCaptureDate": "2016-06-08 18:17:33.000",
    "endCaptureDate": "2016-06-08 18:20:33.000",
    "name": "My new DataChannel",
    "type": "search"
}
```


Example invocation:

```bash
curl -X POST -d @new-datachannel.json -H "Content-Type: application/json"  http://gatezkt1:8080/CaptureREST/rest/datachannel/

```


Example response:

```
> POST /CaptureREST/rest/datachannel/ HTTP/1.1
> User-Agent: curl/7.35.0
> Host: gatezkt1:8080
> Accept: */*
> Content-Type: application/json
> Content-Length: 302
> 
* upload completely sent off: 302 out of 302 bytes
< HTTP/1.1 200 OK
< Content-Type: application/json
< Transfer-Encoding: chunked
< Server: Jetty(9.2.2.v20140723)

```

```json
{"state":"200 OK","data":{"_global_id":"ec473cd3"}}

```

The return value is the status of the operation and the ID of the newly created Data Channel.

### GET /datachannel/{id}

This method get a Data Channel by Id from the Capture storage.

Endpoint:

```
http://gatezkt1:8080/CaptureREST/rest/datachannel/{id}
```



Media types formats:

*  application/json
*  application/xml

Parameters:

The ID of the Data Channel is provided in URL


Example invocation:

```bash
curl -X GET -H "Accept: application/json" 'http://gatezkt1:8080/CaptureREST/rest/datachannel/ec473cd3'
```


Example response:

```
> GET /CaptureREST/rest/datachannel/ec473cd3 HTTP/1.1
> User-Agent: curl/7.35.0
> Host: gatezkt1:8080
> Accept: application/json
> 
< HTTP/1.1 200 OK
< Content-Type: application/json
< Transfer-Encoding: chunked
* Server Jetty(9.2.2.v20140723) is not blacklisted
< Server: Jetty(9.2.2.v20140723)
< 

```

```json
{
  "dataChannelState": null,
  "maxHistoricalTweetLimit": null,
  "tweetRate": null,
  "status": "active",
  "endCaptureDate": "2016-06-08 18:20:33.000",
  "startCaptureDate": "2016-06-08 18:17:33.000",
  "dataSources": [
    {
      "twitter": {
        "chronologicalOrder": false,
        "fromLastTweetId": false,
        "lastTweetId": 0,
        "keywords": "carbonero casillas",
        "type": "Twitter",
        "sourceID": "0792cc8d-53d2-4eeb-8ba6-a6785db2e7ba"
      }
    }
  ],
  "totalTweetCount": null,
  "channelID": "ec473cd3",
  "name": "Test",
  "type": "search",
  "description": "",
  "creationDate": "2016-10-19 17:37:44.675",
  "updateDate": "2016-10-19 17:37:44.675"
}


```


### PUT /datachannel/{id}

This method updates Data Channel with the given {id}.

Endpoint:

```
http://gatezkt1:8080/CaptureREST/rest/datachannel/{id}
```

Media types formats:

*  application/json
*  application/xml

Parameters:

- The ID of the Data Channel is provided in URL
- Data Channel is provided in the PUT payload. The Datachannel format is the same as retrieved from the GET method.
- Note that it is mandatory to use sourceID in the "dataSources" in order to update concrete Data Source keywords, otherwise a new DataSource object will be added. Yuo can use the [GET method](#get-/datachannel/{id}) to retireve necessary IDs. The easiest way is to simply use the response from the [GET method](#get-/datachannel/{id}) and modify necessary fields.

JSON input for the PUT method:
```json
{

    "dataSources": [
        {
            "twitter": {
                "keywords": "carbonero casillas updated",
                "type": "Twitter",
				"sourceID": "0792cc8d-53d2-4eeb-8ba6-a6785db2e7ba"
            }
        }
    ],
    "endCaptureDate": "2016-06-08 18:20:33.000",
    "name": "Test",
    "startCaptureDate": "2016-06-08 18:17:33.000",
    "type": "search"

}
```

Example invocation:

```bash
curl -X PUT -d @modified-datachannel.json -H "Content-Type: application/json" 'http://gatezkt1:8080/CaptureREST/rest/datachannel/ec473cd3'
```


Example response:

```
> PUT /CaptureREST/rest/datachannel/ec473cd3 HTTP/1.1
> User-Agent: curl/7.35.0
> Host: localhost:8080
> Accept: */*
> Content-Type: application/json
> Content-Length: 363
> 
* upload completely sent off: 363 out of 363 bytes
< HTTP/1.1 200 OK
< Content-Type: application/json
< Transfer-Encoding: chunked
< Server: Jetty(9.2.2.v20140723)

```

```json

{"state":"200 OK","data":{"_global_id":"ec473cd3"}}

```


### DELETE /datachannel/{id}

This method removes the Data Channel with the given {id}. The delete is logical, and removed datachannel will appear in the list of all datachannels with the property "state": "inactive".

Endpoint:

```
http://gatezkt1:8080/CaptureREST/rest/datachannel/{id}
```



Media types formats:

*  application/json
*  application/xml

Parameters:

The ID of the Data Channel is provided in URL.


Example invocation:

```bash
curl -X DELETE -H "Accept: application/json" 'http://gatezkt1:8080/CaptureREST/rest/datachannel/ec473cd3'
```


Example response:

```
> DELETE /CaptureREST/rest/datachannel/ec473cd3 HTTP/1.1
> User-Agent: curl/7.35.0
> Host: gatezkt1:8080
> Accept: */*
> Content-Type: application/json
> 
< HTTP/1.1 200 OK
< Content-Type: application/json
< Transfer-Encoding: chunked
* Server Jetty(9.2.2.v20140723) is not blacklisted
< Server: Jetty(9.2.2.v20140723)
< 

```

```json

{"state":"200 OK","data":{"_global_id":"ec473cd3"}}

```