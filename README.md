# K8s-Kafka-AEPC-Back
application base microservices that manage Addresses, Employees, Projects and Companies. each business microservices of the application is implemented into technical architecture: clean architecture. each writing event (POST, UPDATE, DELETE) is published/distributed using kafka infrastructure.
## business microservices
- Address-microservice 
- Employee-microservice 
- company-microservice
- Project-microservice
## utility microservices
- registry service.
- gateway service.
- configuration service.
- kafka infrastructure: zookeeper, kafka-server, conduktor
## kafka
- a kafka infrastructure is in place for events distribution into topics.
- each writing event in database (POST, DELETE, UPDATE) is distributed into kafka topics.
- conduktor is used as UI for managing and exploring kafka events, kafka servers,...
- avro is used for serialiazing messages sent to topics
## unit tests and deploment
- for each code unit of business microservices is tested with **JUnit5** and **AssertJ**.
- **mockito** is used to mock external unit dependency.
- **KafkaContainer** is used  to test Kafka producer/consumer mock kafka infrastructure.
      - [Testcontainers for Java](https://java.testcontainers.org/modules/kafka/).
- each service (business and utility microservice) is deployed in docker image.
- a docker-compose template is used to deploy all images of the application.
- Jenkins is used for continuous integrataion and deployment (CI/CD). After each git commit, Jenkins launch automatically following jobs:
  - a build of each microservice.
  - unit tests of all business microservices and published a report of passed, skipped and fail tests.
  - package each microservice and publish a related jar file.
  - build a docker image of each microservice refering to a dockerfile defined inside microservice.
  - publish docker images in docker registry.
  - run docker images of microservices of application in docker containers.
 
    ### ci-cd summary

![my-ci-cd-flow](https://github.com/placidenduwayo1/k8s-kafka-aepc-back/assets/124048212/35dcc453-9f99-48bf-b4ee-505ee4230cdd)

 ### deployed microservices in docker images
 - kafka infrastructure:
  - zookeeper (one instance) for managing kafka brokers
  - kafka server (three instances)
  - kafdrop (one instance) for web UI to view kafka brokers, topics and events produced
- utility microservices:
  - a config service for managing and externalize services configuration
  - a registry service to serve gateway service and business services to registry with their name
  - a gateway service 
- business microservices:
  - bs-ms-address
  - bs-ms-employee
  - bs-ms-company
  - bs-ms-project
### application architecture
![k8s-containers-orchestration](https://github.com/placidenduwayo1/k8s-kafka-aepc-back/assets/124048212/e6e5cdd7-54c8-4920-a1e5-e89d0e5a91e7)

## architecture kafka inside business microservice
- a model is a java bean that is sent as payload using a REST api, a spring service build a kafka message with the model.
- a spring service uses kafka producer to send the kafka message to kafka topic.
- a spring service uses kafka consumer to subscribe to the kafka topic and consumes the message from topics that it sends to another spring service either to persist it in db.
  ### kafka infra summary    
![kafka](https://github.com/placidenduwayo1/k8s-kafka-aepc-back/assets/124048212/8ed4f7dc-355c-4dcf-b3fb-5cc5a2fb4a85)
![kafka](https://github.com/placidenduwayo1/k8s-kafka-aepc-back/assets/124048212/f26f3105-b079-43e2-ba25-67f837d4e881)
