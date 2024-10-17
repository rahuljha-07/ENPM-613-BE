# ilim-backend

Welcome to the **ilim-backend**! 
So far, the server provides authentication and user management services using AWS Cognito, AWS SDK, and PostgreSQL, with good exception handling.

---
## Project Overview

The **ilim** application is a backend service that handles user authentication and profile management. It leverages:

- **AWS Cognito** for user authentication and authorization.
- **AWS SDK for Java** to interact with AWS services.
- **Spring Boot** as the application framework.
- **PostgreSQL** as the local development database.
- **AWS CloudFormation** to provision AWS resources.
- **Docker Compose** for the DB in the local env.

---

## Prerequisites

Before you begin, ensure you have the following installed on your local development machine:

- **Java Development Kit (JDK) 17** or higher
- **Maven** for building the project
- **Git** for version control
- **AWS CLI** for interacting with AWS services
- **Docker** (optional, for running PostgreSQL locally)
- **An AWS Account** See the How-To document at `./devops/setting-up-aws`

---

## Application Configuration

Configure the application by setting the necessary properties.

### Using `application.properties`

Create the `application-local.properties` file in the `src/main/resources` directory to set up your local env.

Paste this and replace the placeholders in double {}

```yaml
spring.datasource.url=jdbc:postgresql://localhost:5432/ilim-local-db
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA and Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Cognito (follow the tutorial in ./devops/setting-up-aws.md)
aws.cognito.userPoolId={{USER_POOL_ID}}
aws.cognito.clientId={{CLIENT_ID}}
aws.cognito.region=us-east-1
aws.cognito.jwkUrl={{JWK_URL}}

```

## Running the Application

### Setting Up the Local Database

For local development, you can use a local PostgreSQL database.

1. **Go to `./devops/local-db` directory.

2. **Start the PostgreSQL Container**:

   ```bash
   docker-compose up -d
   ```

---

## Testing and Development

- **API Endpoints**: You can test the API endpoints using tools like **Bruno** or **Postman**.
- The project will add Swagger-UI to expose all endpoints and their details in a nice GUI.

---

## Security Considerations

- **HTTPS**:

  - For production, ensure the application uses HTTPS to encrypt data in transit.

- **Cognito User Pool**:

  - The free tier of AWS Cognito is 50-sign up per day. Do not exceed that to stay free of charge.


## Implementation Details

- User(name, email, birthdate) are unmodifiable in the current implementation, because this is how we defined the Cognito stack, this can be changed if needed.
- When a user account gets verified, it gets created in the database, otherwise it will stay in cognito as 'unconfirmed'.

---

**Development Approach:**

- **Development Environment:**
  - Run the server and database locally.
  - Use local configurations and keep your development cycle efficient.

- **Production Environment:**
  - We will deploy the server to an EC2 instance in a public subnet.
  - Will put the RDS instance in a private subnet.
  - Will secure resources using security groups and IAM roles.

- **Common in both Environment**:
  - AWS Cognito
  - AWS S3

- **Why we have two different DB instances in the local and dev environments?**
    - Avoid any credential cost in AWS.
    - Much faster and simpler development, since you don't need to wait for every little change to get deployed to AWS EC2.
    - Much more secure since this way we won't need to expose AWS RDS publicly.