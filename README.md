# Doctor Appointment System

This project is a web-based platform designed to streamline the process of scheduling, managing, and tracking medical appointments. It serves as a central hub connecting patients, doctors, and administrative staff.

## Key Features

* **Role-Based Access Control (RBAC):** The system provides distinct functionalities and views for three user roles: Admin, Doctor, and Patient.
* **User Management:** It includes secure user registration, authentication, and profile management. Passwords are kept secure by being hashed using BCrypt.
* **Appointment Management:** The system allows for end-to-end scheduling, viewing, updating, and cancellation of appointments. It incorporates business logic to check for doctor availability and prevent double bookings.
* **Personalized Dashboards:** Each user role has a dedicated dashboard to manage their specific tasks.
* **SMS Notifications:** Automated SMS reminders are sent for upcoming appointments through external gateways like Twilio.

## System Architecture

The application is built as a **Monolithic Application** using the **Model-View-Controller (MVC)** architectural pattern. This design was chosen to enhance development speed and simplify the initial deployment. The backend is responsible for all business logic, data access, and server-side rendering of the user interface.

### Architecture Diagram

The system architecture involves the user's browser interacting over HTTPS with a cloud infrastructure that includes a load balancer. The load balancer directs traffic to the Spring Boot application server. The application logic, structured in layers (Controllers, Services, Repositories), handles business operations and data access. It communicates with a database server and external services for functionalities like SMS notifications.

### Technology Stack

| Component | Technology | Justification |
| :--- | :--- | :--- |
| **Backend** | Java 17, Spring Boot | A robust, secure, and highly scalable enterprise-grade framework. |
| **Data Access** | Spring Data JPA / Hibernate | This simplifies database interactions and promotes the reusability of code. |
| **Frontend** | HTML5, CSS3, Thymeleaf | Enables server-side rendering for a tightly integrated view logic. |
| **Database** | PostgreSQL / MySQL | These are reliable, ACID-compliant relational databases that ensure data integrity. |
| **Build Tool** | Apache Maven | The standard for managing dependencies and building Java projects. |
| **Security** | Spring Security | A comprehensive framework for authentication and access control. |

## Component Design

The application is logically divided into the following core modules:

* **User Management & Authentication:** This module handles all aspects of user identity, including registration, login, and authorization using Spring Security.
* **Appointment Management:** This is the core business domain of the application, managing the creation, modification, and status tracking of appointments.
* **Notification Service:** This component is responsible for sending communications to users, primarily timely SMS reminders for upcoming appointments
