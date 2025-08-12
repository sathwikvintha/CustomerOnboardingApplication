Customer Onboarding Backend Workflow
📌 Overview
This project simulates the backend of a customer onboarding workflow for a banking application.
It is designed as a microservices-based system with REST APIs for:
•	Customer registration
•	KYC document submission & verification
•	Account creation after KYC approval
A lightweight UI is optional — API calls can be demonstrated using Postman and Swagger UI.
________________________________________
🚀 Features
1.	Customer Registration
o	Register customers with personal and identification details.
o	Validations for email, phone, DOB, PAN, Aadhaar, etc.
o	System-generated customer ID returned after registration.
2.	KYC Document Submission
o	Upload PAN, Aadhaar, and Photograph (mandatory).
o	Accept documents as Base64 or metadata.
o	Store metadata securely.
3.	KYC Verification
o	Admin can verify/reject KYC with remarks.
o	Customers notified via email on status change.
o	API to fetch current KYC status.
4.	Account Creation
o	Create a new bank account after KYC approval.
o	Generate mock account number.
o	Link account to customer and store account details.
________________________________________
🛠 Tech Stack
Component	Technology Used
Language	                      Java
Framework	                Spring Boot
Database	                 Oracle DB
Messaging	Apache Kafka (optional/mocked)
API Docs	           Swagger / OpenAPI
Testing	                   Postman
________________________________________
📂 Microservices
•	Customer Service – Handles customer registration and details.
•	KYC Service – Manages KYC document upload & verification.
•	Account Service – Creates and manages customer accounts.
•	Admin Service – Manages admin functions (verification, status updates, audit logs).
________________________________________
⚙️ Setup & Installation
1. Clone the Repository
git clone https://github.com/sathwikvintha/CustomerOnboardingApplicationProject
cd sathwikvintha/CustomerOnboardingApplicationProject
2. Configure Database
•	Create an Oracle DB instance.
•	Update application.properties in each service with your DB connection:
spring.datasource.url=jdbc:oracle:thin:@//localhost:1521/ORCL
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
3. Run Services
Each service can be run independently:
mvn spring-boot:run
4. Access API Docs
•	Swagger UI: http://localhost:{port}/swagger-ui.html
•	API JSON Spec: http://localhost:{port}/v3/api-docs
________________________________________


📦 API Endpoints (Summary)
Service	   Method	         Endpoint	         Description
Customer	    POST	 /customers/register	Register a new customer
KYC	    POST	 /kyc/upload	Upload KYC documents
KYC	     GET	 /kyc/status/{customerId}	Get KYC status
Admin	     PUT	 /admin/kyc/{kycId}/verify	Verify KYC
Account	    POST	 /account/create	Create account after KYC
________________________________________
🗄 Database Design
Main Tables
•	customers – Stores customer personal info.
•	kyc_documents – Metadata for KYC files.
•	kyc_status – Status tracking (PENDING, VERIFIED, REJECTED).
•	accounts – Account details linked to customers.
•	activity_log – Audit log for admin actions.
________________________________________
🔄 Flow of Operations
1.	Customer Registration → Generates customerId.
2.	KYC Document Upload → Metadata stored, status = PENDING.
3.	Admin Verification → Status updated to VERIFIED or REJECTED.
4.	Account Creation → Only allowed for VERIFIED customers.
________________________________________
📜 Deliverables
•	Codebase – Source code for all services.
•	API Documentation – Swagger/OpenAPI spec.
•	Postman Collection – For quick testing.
•	Database ER Diagram – Entity relationships.
•	Design Document – Architecture & data flow.
•	Demo Script – API execution steps with sample responses.
•	Test Cases – Covering all major scenarios.
•	README – (this file).
________________________________________

✅ Testing
Use the provided Postman collection to test all endpoints.
Ensure services are running and connected to the database before testing.
________________________________________
👨‍💻 Authors
•	Your Team Name / Members – Developers of the project.
