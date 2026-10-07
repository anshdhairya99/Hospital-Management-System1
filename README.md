# 🏥 Hospital Management System

A **Hospital Management System** developed using **Java, Spring Boot, and JPA/Hibernate** to simplify and automate hospital operations. The system provides a structured platform for managing patients, doctors, appointments, medical records, and other hospital-related information.

## 📌 Project Overview

The Hospital Management System is a backend-based application designed to reduce manual work and efficiently manage hospital data.

It uses **Spring Boot** for developing RESTful APIs and **JPA/Hibernate** for database connectivity, entity mapping, and CRUD operations.

## 🚀 Technologies Used

* **Java**
* **Spring Boot**
* **Spring Data JPA**
* **JPA/Hibernate**
* **MySQL**
* **Maven**
* **REST API**
* **Postman** for API testing

## ✨ Key Features

* 👨‍⚕️ Doctor Management
* 🧑‍🤝‍🧑 Patient Management
* 📅 Appointment Management
* 🏥 Department Management
* 💊 Medical Record Management
* 🔐 Role-Based Access Control (RBAC)
* ➕ Add hospital records
* 🔍 Search and retrieve records
* ✏️ Update existing records
* 🗑️ Delete records
* 🔄 CRUD operations using JPA/Hibernate
* 🛡️ Exception handling and validation

## 👥 User Roles

The system can support different roles such as:

* **Admin** – Manage doctors, patients, departments, and users.
* **Doctor** – View patient information and manage medical records.
* **Receptionist** – Manage patients and appointments.
* **Patient** – View appointments and personal medical information.

## 🏗️ Project Architecture

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
JPA / Hibernate
    ↓
MySQL Database
```

### Layers

**Controller Layer**
Handles HTTP requests and REST API endpoints.

**Service Layer**
Contains business logic and application rules.

**Repository Layer**
Performs database operations using Spring Data JPA.

**Entity Layer**
Defines database entities and their relationships using JPA annotations.

## 🗃️ Core Entities

The major entities of the system include:

```text
Patient
Doctor
Appointment
Department
MedicalRecord
User
Role
```

Example relationships:

```text
Department → Doctors
Doctor → Appointments
Patient → Appointments
Patient → Medical Records
User → Roles
```

## 🔄 CRUD Operations

The application supports standard CRUD operations:

| Operation | Description |
| --------- | ----------- |
| Create    | Ad          |
