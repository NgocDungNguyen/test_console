# 🏠 Rental Management System

![Rental Management System Banner](https://via.placeholder.com/1200x300?text=Rental+Management+System)

[![Java Version](https://img.shields.io/badge/Java-17%2B-blue.svg)](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
[![Maven](https://img.shields.io/badge/Maven-3.6.0%2B-orange.svg)](https://maven.apache.org/download.cgi)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

> A comprehensive solution for managing rental properties, tenants, and agreements with ease and efficiency.

## 📚 Table of Contents

- [🌟 Features](#-features)
- [🛠️ Prerequisites](#️-prerequisites)
- [🚀 Quick Start](#-quick-start)
- [💻 Running the Application](#-running-the-application)
- [🏗️ Project Structure](#️-project-structure)
- [📊 Data Management](#-data-management)
- [🧪 Testing](#-testing)
- [🤝 Contributing](#-contributing)
- [📄 License](#-license)
- [📞 Support](#-support)

## 🌟 Features

Our Rental Management System offers a robust set of features to streamline your property management tasks:

- 🏘️ **Property Management**: Handle both residential and commercial properties
- 👥 **Tenant & Owner Tracking**: Maintain detailed records of tenants and property owners
- 📝 **Agreement Handling**: Create, update, and manage rental agreements effortlessly
- 💰 **Financial Oversight**: Track payments and generate comprehensive financial reports
- 📊 **Occupancy Analysis**: Get insights into property occupancy rates
- 📈 **Reporting Suite**: Generate various reports including income, tenant status, and property analytics

## 🛠️ Prerequisites

Before diving in, make sure you have the following tools installed:

- ☕ Java Development Kit (JDK) 17 or later
- 🔧 Maven 3.6.0 or later
- 🐙 Git (for version control and cloning the repository)

## 🚀 Quick Start

Get up and running with these simple steps:

1. **Clone the repository**
   ```bash
   git clone https://github.com/NgocDungNguyen/test.git
   cd test
   ```

2. **Build the project**
   ```bash
   mvn clean install
   ```

## 💻 Running the Application

### 🧠 IntelliJ IDEA

1. Open IntelliJ IDEA
2. Select `File` > `Open` and navigate to the project directory
3. Wait for IntelliJ to import the project and resolve dependencies
4. Locate `src/main/java/com/rentalsystem/ui/ConsoleUI.java`
5. Right-click on the `ConsoleUI` class and select "Run 'ConsoleUI.main()'"

### 🖥️ Visual Studio Code

1. Launch Visual Studio Code
2. Choose `File` > `Open Folder` and select the project directory
3. Install the "Extension Pack for Java" if not already installed
4. Open the Command Palette (Ctrl+Shift+P or Cmd+Shift+P on macOS)
5. Type "Java: Configure Java Runtime" and select it
6. Choose JDK 17 or later
7. Navigate to `src/main/java/com/rentalsystem/ui/ConsoleUI.java`
8. Click the "Run" button above the `main` method or use the F5 shortcut

## 🏗️ Project Structure

Our project is organized for clarity and maintainability:

```
src/
├── main/
│   ├── java/com/rentalsystem/
│   │   ├── config/       # Configuration classes
│   │   ├── manager/      # Business logic layer
│   │   ├── model/        # Entity classes
│   │   ├── ui/           # User interface components
│   │   └── util/         # Utility classes and helpers
│   └── resources/
│       └── data/         # Sample data files
└── test/
    └── resources/
        └── test-data/    # Test data files
            ├── test_hosts.txt
            ├── test_properties.txt
            └── test_rental_agreements.txt
```

## 📊 Data Management

The system uses the following data files located in `src/main/resources/data/`:

### Core Data Files

| File Name | Description |
|-----------|-------------|
| `hosts.txt` | Host information |
| `tenants.txt` | Tenant details |
| `owners.txt` | Property owner records |
| `properties.txt` | Property listings |
| `rental_agreements.txt` | Rental agreement data |
| `payments.txt` | Payment transaction records |

### Additional Relationship Files

| File Name | Description | Purpose |
|-----------|-------------|---------|
| `properties_hosts.txt` | Property-Host associations | Allows for many-to-many relationships between properties and hosts |
| `properties_tenants.txt` | Property-Tenant relationships | Represents current occupancy of properties |
| `rental_agreements_tenants.txt` | Agreement-Tenant links | Allows for multiple tenants per agreement (e.g., roommates) |

### Why These Additional Files?

1. **Data Normalization**: Reduces data redundancy and improves data integrity.
2. **Flexibility**: Allows for complex relationships without complicating main entity structures.
3. **Performance**: Enables faster lookups for specific relationships in large datasets.
4. **Easier Updates**: Simplifies the process of updating specific relationships.

Note: The necessity of these additional files depends on the specific requirements, data scale, and complexity of your system. For simpler implementations, the core files may be sufficient.

## 🧪 Testing

Our project includes a set of unit tests to ensure the reliability and correctness of core functionalities. The test data files are located in `src/test/resources/test-data/`.

To run the tests, use the following Maven command:

```bash
mvn test
```

This will execute all unit tests and provide a report on the test results.

## 🤝 Contributing

We welcome contributions to the Rental Management System! Here's how you can help:

1. Fork the repository
2. Create your feature branch: `git checkout -b feature/AmazingFeature`
3. Commit your changes: `git commit -m 'Add some AmazingFeature'`
4. Push to the branch: `git push origin feature/AmazingFeature`
5. Open a pull request

Please read [CONTRIBUTING.md](CONTRIBUTING.md) for details on our code of conduct and the process for submitting pull requests.

## 📄 License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details.

## 📞 Support

Encountering issues or have questions? We're here to help!

- 📧 Email: s3978535@rmit.edu.vn
- 💬 Chat: [Join our Discord community](https://discord.gg/rentalmanagementsystem)
- 🐦 Twitter: [@RentalManSys](https://twitter.com/RentalManSys)

---

<p align="center">
  Made with ❤️ by the Rental Management System Team
</p>