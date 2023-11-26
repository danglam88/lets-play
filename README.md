# Java LetsPlay Project

## Table of Contents

- [Description](#description)
- [Installation](#installation)
- [Usage](#usage)
- [License](#license)
- [Contributing](#contributing)
- [Author](#author)

## Description

This is a Java project that implements a basic CRUD (Create, Read, Update, Delete) API using Spring Boot with MongoDB.

The application contains user management and product management functionalities.

A user can have either the `ADMIN` (called an admin) or the `USER` (called a user) role.

The list of REST APIs to perform CRUD operations on both Users and Products are:

- POST `/auth` - Authenticate a user by their username (email) and password then return a 7-day valid token to them (accessible without authentication)
- POST `/reg` - Register a new user with `ADMIN` or `USER` role (accessible without authentication)
- GET `/products` - Get all products in the database (accessible without authentication)
- GET `/products/{id}` - Get a product by its ID (accessible by an admin or a user)
- POST `/products` - Create a new product (accessible by an admin only)
- PUT `/products/{id}` - Update a product by its ID (accessible only by an admin who owns that product)
- DELETE `/products/{id}` - Delete a product by its ID (accessible only by an admin who owns that product)
- GET `/users` - Get all users in the database (accessible by an admin or a user)
- GET `/users/{id}` - Get a user by their ID (accessible by an admin or a user who owns that account)
- PUT `/users/{id}` - Update a user by its ID (accessible by an admin or a user who owns that account)
- DELETE `/users/{id}` - Delete a user by its ID (accessible by an admin or a user who owns that account)

An example of a valid JSON object for creating a new / updating an existing product is:

```json
{
    "name": "Name of Product",
    "description": "Description of Product",
    "price": 100.0,
    "userId": "a1b2c3d4"
}
```

An example of a valid JSON object for creating a new / updating an existing user is:

```json
{
    "name": "Name of User",
    "email": "abcxyz@gmail.com",
    "password": "AbcXyz1@",
    "role": "ADMIN"
}
```

An example of a valid JSON object for authenticating a user is:

```json
{
    "username": "abcxyz@gmail.com",
    "password": "AbcXyz1@"
}
```

## Installation

To install the project, clone the repository to your local machine.

```bash
git clone git@github.com:danglam88/lets-play.git
```

Make sure you have Java and Maven installed and configured properly on your machine.

You can download Java [here](https://www.oracle.com/java/technologies/javase-downloads.html).

You can download Maven [here](https://maven.apache.org/download.cgi).

## Usage

To run the project, navigate to the project root directory containing the `pom.xml` file, then build the project (using Maven) and run the JAR output (using Java).

```bash
mvn clean package
```

```bash
java -jar target/*.jar
```

This will start the server at `https://localhost:8443/` and the above-mentioned APIs are ready for use.

Postman is a great tool for testing REST APIs. You can download it [here](https://www.postman.com/downloads/).

## License

This project is licensed under the MIT License. See the LICENSE file for more details.

## Contributing

Contributions are welcome. Please open an issue or submit a pull request if you would like to help improving the project.

## Author

- [Danglam](https://github.com/danglam88)
