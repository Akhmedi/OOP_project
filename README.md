# OOP_project

Overview
University Management System is a console-based Java application developed for the Object-Oriented Programming course at KBTU. The project simulates the internal university ecosystem and supports multiple user roles including students, teachers, managers, administrators, and researchers.
The system demonstrates practical implementation of Object-Oriented Programming principles, custom exception handling, serialization, and software design patterns.
Main Functionalities:
-Student
-Register for courses
-View transcript and GPA
-Track academic performance
-Assign research supervisor
-Teacher
-Manage courses
-Assign grades
-View enrolled students
-Participate in research activities
-Manager
-Approve registrations
-Publish university news
-Manage schedules
-View rankings and statistics
-Admin
-Add, remove, and update users
-Manage departments
-View logs and system activity
-Research Subsystem
-Research projects
-Research papers
-Researcher profiles
-Join requests for projects
-h-index calculation
-OOP Principles Used
-Inheritance
-Encapsulation
-Polymorphism
-Abstraction
-Design Patterns
-Singleton
-DataStorage manages serialization, deserialization, and system-wide data access

Observer
Managers publish news notifications to subscribed users

Factory
ResearcherFactory creates researcher objects for different user types
System Architecture
Main Classes
User
Student
Teacher
Manager
Admin
Employee
Researcher
Course
Transcript
Mark
Lesson
ResearchProject
ResearchPaper
Packages
users
academic
research
patterns
exceptions
enums
utils
Technologies
Java
Java Collections Framework
Java Serialization
Exception Handling
OOP Concepts
Custom Exceptions
AuthenticationException
MaxCreditsException
InvalidCourseRegistrationException
LowHIndexException
NotResearcherException
SupervisorAssignmentException
RoomOccupiedException
Data Persistence
The system uses Java object serialization to store and restore data between sessions using .ser files.
Team Members
Akhmedi Orynbassar
Akezhan Sarkyt
Dizhan Mashirov
Bexultan Turar
Conclusion
The University Management System is a modular Java application that demonstrates real-world usage of Object-Oriented Programming concepts and software design patterns. The project provides a scalable and maintainable architecture for managing university processes in a console-based environment.
