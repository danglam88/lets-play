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

A user can have either the `ROLE_ADMIN` (called an admin) or the `ROLE_USER` (called a user) role.

The list of RESTful APIs to perform CRUD operations on both Users and Products are:

- POST `/auth` - Authenticate a user by their username (email) and password then return a 7-day valid token to them (accessible without authentication)
- POST `/reg` - Register a new user with `ROLE_USER` role (accessible without authentication)
- GET `/products` - Get all products in the database (accessible without authentication)
- GET `/products/{id}` - Get a product by its ID (accessible without authentication)
- POST `/products` - Create a new product (accessible by an admin or a user)
- PUT `/products/{id}` - Update a product by its ID (accessible by an admin or a user)
- DELETE `/products/{id}` - Delete a product by its ID (accessible by an admin or a user)
- GET `/users` - Get all users in the database (accessible by an admin or a user)
- GET `/users/{id}` - Get a user by their ID (accessible by an admin or a user)
- POST `/users` - Create a new user (accessible by an admin only)
- PUT `/users/{id}` - Update a user by its ID (accessible by an admin only)
- DELETE `/users/{id}` - Delete a user by its ID (accessible by an admin only)
- GET `/ownUserInfo` - Get the information of the currently authenticated user (accessible by an admin or a user)
- GET `/ownProductInfo` - Get all products owned by the currently authenticated user (accessible by an admin or a user)

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
    "password": "123456",
    "role": "ROLE_ADMIN"
}
```

An example of a valid JSON object for authenticating a user is:

```json
{
    "username": "abcxyz@gmail.com",
    "password": "123456"
}
```

An example of a valid JSON object for registering a new user is:

```json
{
    "name": "Name of User",
    "email": "abcxyz@gmail.com",
    "password": "123456"
}
```

## Installation

To install the project, clone the repository to your local machine.

```bash
git clone https://github.com/danglam88/LetsPlay.git
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

Postman is a great tool for testing RESTful APIs. You can download it [here](https://www.postman.com/downloads/).

## License

This project is licensed under the MIT License. See the LICENSE file for more details.

## Contributing

Contributions are welcome. Please open an issue or submit a pull request if you would like to help improving the project.

## Author

- [Danglam](https://github.com/danglam88)
