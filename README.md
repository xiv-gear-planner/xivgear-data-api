# Xivgear Data API

This service provides the reference data for Xivgear, including:
* BaseParams
* Items
* Food
* ItemLevels
* Jobs/Classes
* Materia

## Architecture

This is a Micronaut-based service that uses xivapi-java to load data from xivapi.
Data is loaded in bulk and persisted in object storage. Upon startup, the service
will load the data from object storage (if it exists), and attempt to load fresh
data from xivapi. Periodically, the service will refresh from both object storage
and xivapi, to see if newer data exists. Periodic refreshes from xivapi simply
check the schema and game versions, rather than pulling the entire data set.
The full data set is only pulled if a change is detected. This means that the ongoing
load on xivapi is very low.

Endpoints that return data extend from `BaseDataEndpoint` which handles checking
the `If-Modified-Since` header and returning a 304 if the data hasn't changed. In
addition, they will return a 503 is the data has not been successfully loaded yet.
