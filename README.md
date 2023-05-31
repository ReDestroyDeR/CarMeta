# CarMeta
Car Information telegram bot that scrapes Russian car services 

This is my final project for "Scala Developer" course at Otus.

## Architecture

![architecture](https://github.com/ReDestroyDeR/CarMeta/assets/24751213/8ace47c0-a9d9-4f4f-b52c-845013f20f22)

Done:

\[ x ] Scraping Service (AutoRussia only, no Advertisement suport)<br>
\[ x ] Aggregation Service<br>
\[ - ] Warehouse<br>
\[ - ] Telegram Frontend

Project is built using SBT

Service decomposition is done poorly, development is done like it's Mutli-Module Monolith, but .jars should be deployed separetely

## Technologies

* Scala 2.13
* Cats Effect 3
* fs2
* Ciris
* Circe
* ScalaPB
* Kafka
* Redis
* Elasticsearch

## Run

Docker compose includes Kafka, Confluent Control Panel, Redis, Kibana and Elasticsearch. You can run these dependencies by running `docker compose up -d`

Start scraper and aggregation services, you should see that Scraper is scraping data from AutoRussia and Aggregation service is getting records from definitions topic.
Data Flow could be seen in Control Panel, after that, Definitions of cars could be seen in Kibana.
