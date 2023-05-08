# ChatterGPT API Backend

https://chattergpt.net

ChatterGPT API Backend is a backend application that simplifies the integration of OpenAI's API with various applications. By managing user accounts, credits, requests, and completions, it streamlines analytics and troubleshooting. Users can purchase credits through an associated frontend, removing the need for end-users to manage API keys and enabling service providers to charge for usage.

API users are authenticated using JWT tokens obtained from Google's OAuth2.0 repository server. MariaDB serves as the database to store user information and request/completion logs. The backend is developed using Kotlin and the Spring Boot framework, ensuring efficiency and performance. The frontend, a simple HTML page with JavaScript, handles login/logout and credit purchasing functionalities. Payments are securely processed through [Stripe](https://stripe.com).

# Deployment & Configuration

## Docker

A `dockerfile` is included to build the backend as a Docker image. Use the following command to build the image:

```docker build -t image_name:tag .```

## Docker Compose

### Configuration

The Docker Compose file requires a set of environment variables to be defined in the path. These variables can be found in the dockercompose/docker-compose.yml file.

The provided Docker Compose file sets up the backend and the database, ensuring that everything necessary for running the application is in place. It is configured to work with [acme-companion](https://github.com/nginx-proxy/acme-companion) and [nginx-proxy](https://github.com/nginx-proxy/nginx-proxy) for SSL encryption and reverse proxying.

To start the service using Docker Compose, navigate to the dockercompose directory and run the following command:

```docker-compose up -d```

This will launch the ChatterGPT API Backend and associated services in detached mode, allowing you to monitor logs and manage the application as needed.

