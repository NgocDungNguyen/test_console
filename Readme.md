# 🏢 Rental Property Management System

![Rental Management System Banner](https://via.placeholder.com/1200x300?text=Rental+Property+Management+System)

[![Java Version](https://img.shields.io/badge/Java-17%2B-blue.svg)](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
[![Maven](https://img.shields.io/badge/Maven-3.6.0%2B-orange.svg)](https://maven.apache.org/download.cgi)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

## 📋 Overview

The Rental Property Management System is a robust, Java-based application designed to streamline the complexities of property management. It offers a comprehensive solution for managing rental properties, tenants, owners, and rental agreements, with a focus on data integrity and efficient operations.

## 🌟 Key Features

- 🏘️ **Multi-entity Management**: Seamlessly handle Properties, Tenants, Owners, Hosts, and Rental Agreements.
- 🏠🏢 **Flexible Property Types**: Support for both Residential and Commercial properties with type-specific attributes.
- 📄 **Advanced Rental Agreement Handling**: Manage complex scenarios including multi-tenant agreements and subletting.
- 💰 **Financial Tracking**: Integrated payment system with support for various payment methods and detailed transaction logging.
- 📊 **Reporting and Analytics**: Generate insightful reports on occupancy rates, financial performance, and more.
- 💾 **Data Persistence**: Efficient file-based data storage with automatic data synchronization.

## 🔧 Technical Specifications

### 🏗️ Architecture

The system follows a layered architecture to ensure separation of concerns:

1. 🖥️ **Presentation Layer**: Console-based UI (`ConsoleUI` class)
2. 🧠 **Business Logic Layer**: Manager classes (e.g., `PropertyManager`, `TenantManager`)
3. 💽 **Data Access Layer**: `FileHandler` for data persistence
4. 🏛️ **Model Layer**: Entity classes representing core domain objects

### 🎨 Design Patterns

- 🔒 **Singleton**: Utilized for manager classes to ensure a single point of access for each entity type.
- 🔀 **Strategy**: Implemented in the sorting mechanisms for flexible and extensible sorting options.
- 👀 **Observer**: Used for real-time updates between interconnected entities (e.g., Property and RentalAgreement).

### 📊 Data Model

The system's core entities and their relationships:

- 🏠 `Property` (abstract base class)
   - 🏡 `ResidentialProperty`
   - 🏢 `CommercialProperty`
- 👤 `Person` (abstract base class)
   - 🧑‍🤝‍🧑 `Tenant`
   - 👨‍💼 `Owner`
   - 🧑‍🔧 `Host`
- 📜 `RentalAgreement`
- 💳 `Payment`

### 💾 Data Persistence

Data is persisted using a custom CSV-like format, with each entity type having its dedicated file:

- 🏠 `properties.txt`: Property data
- 🧑‍🤝‍🧑 `tenants.txt`: Tenant information
- 👨‍💼 `owners.txt`: Owner details
- 🧑‍🔧 `hosts.txt`: Host records
- 📜 `rental_agreements.txt`: Rental agreement data
- 💳 `payments.txt`: Payment transaction logs

## 🚀 Getting Started

### 📋 Prerequisites

- ☕ JDK 17 or later
- 🛠️ Maven 3.6.0 or later
- 🐙 Git (for version control)

### 🔧 Installation

1. Clone the repository:
   ```
   git clone https://github.com/RMIT-Vietnam-Teaching/further-programming-assignment-1-build-a-console-app-NgocDungNguyen.git
   cd test
   ```

2. Build the project:
   ```
   mvn clean install
   ```

### 🏃‍♂️ Running the Application

Execute the main class `com.rentalsystem.ui.ConsoleUI`:

```
java -cp target/rental-property-management-system-1.0-SNAPSHOT.jar com.rentalsystem.ui.ConsoleUI
```

## 👨‍💻 Development

### 📁 Project Structure

```
src/
├── main/
│   ├── java/com/rentalsystem/
│   │   ├── config/       # Application configuration
│   │   ├── manager/      # Business logic and entity management
│   │   ├── model/        # Domain model classes
│   │   ├── ui/           # User interface components
│   │   └── util/         # Utility classes and helpers
│   └── resources/
│       └── data/         # Sample data files
└── test/
    ├── java/com/rentalsystem/  # Unit and integration tests
    └── resources/
        └── test-data/    # Test-specific data files
```

### 📏 Coding Standards

- Follow Java Code Conventions
- Use meaningful variable and method names
- Write comprehensive JavaDoc comments for public methods and classes
- Maintain a clear separation of concerns between layers

### 🧪 Testing

The project includes JUnit tests for core functionalities. Run tests using:

```
mvn test
```

## 🚀 Performance Considerations

- 🐌 Lazy loading of related entities to minimize memory usage
- 🔍 Indexed collections for faster entity lookups
- 📦 Batch processing for file I/O operations to reduce disk access

## 🔒 Security Measures

- 🛡️ Input validation to prevent injection attacks
- 🔐 Encryption of sensitive data in storage (e.g., payment information)
- 🔑 Role-based access control for different user types (Admin, Host, Tenant)

## 🔮 Future Enhancements

- 💳 Integration with external payment gateways
- 🌐 Implementation of a web-based interface
- 🔔 Real-time notifications system for important events (e.g., upcoming rent due dates)
- 📈 Data analytics dashboard for property performance metrics

## 🤝 Contributing

We welcome contributions to the Rental Property Management System. Please refer to our [Contribution Guidelines](CONTRIBUTING.md) for detailed information on how to submit pull requests, report issues, and suggest improvements.

## 📄 License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for full details.

## 📞 Support and Contact

For technical support or inquiries:

- 📧 Email: s3978535@rmit.edu.vn
- 🐛 Issue Tracker: [GitHub Issues](https://github.com/RMIT-Vietnam-Teaching/further-programming-assignment-1-build-a-console-app-NgocDungNguyen.git)

---

© 2024 Rental Property Management System. All rights reserved.