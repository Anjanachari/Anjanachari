# 1. Java (jdk-1.8.0)
# Use the Java 8 image
FROM java:jdk-1.8.0

# Set the working directory
WORKDIR /app

# Copy the application JAR file to the container
COPY . /app

# Expose the port that the Java application will run on
EXPOSE 8080

# Run the Java application
CMD ["java", "-jar", "your-application.jar"]
###########################################################################
# 2. Tomcat (9.2)
# Use Tomcat 9.2 image
FROM tomcat:9.2

# Copy the WAR file into the Tomcat webapps directory
COPY your-application.war /usr/local/tomcat/webapps/

# Expose the default HTTP port
EXPOSE 8080

# Start Tomcat
CMD ["catalina.sh", "run"]
###########################################################################
# 3. MySQL
# Use the MySQL image
FROM mysql

# Set the environment variables for MySQL (use default root password)
ENV MYSQL_ROOT_PASSWORD=rootpassword

# Expose the MySQL port
EXPOSE 3306
###########################################################################
# 4. Node.js (14)
# Use the Node.js 14 image
FROM node:14

# Set the working directory
WORKDIR /app

# Copy package.json and install dependencies
COPY package*.json /app/
RUN npm install

# Copy the rest of the application code
COPY . /app

# Expose the port that the app will run on
EXPOSE 3000

# Start the Node.js application
CMD ["npm", "start"]
###########################################################################
# 5. Python (3.9)
# Use the Python 3.9 image
FROM python:3.9

# Set the working directory
WORKDIR /app

# Copy the requirements.txt file
COPY requirements.txt /app/

# Install Python dependencies
RUN pip install -r requirements.txt

# Copy the rest of the application code
COPY . /app

# Expose the port the app will run on
EXPOSE 5000

# Run the Python application
CMD ["python", "app.py"]
###########################################################################
# 6. Nginx
# Use the latest Nginx image
FROM nginx:latest

# Copy custom Nginx configuration file
COPY nginx.conf /etc/nginx/nginx.conf

# Copy website files to the Nginx directory
COPY . /usr/share/nginx/html

# Expose port 80 for web traffic
EXPOSE 80

# Start Nginx
CMD ["nginx", "-g", "daemon off;"]
###########################################################################
# 7. Redis
# Use the Redis image
FROM redis:6

# Expose Redis default port
EXPOSE 6379
###########################################################################
# 8. PostgreSQL (13)
# Use the PostgreSQL 13 image
FROM postgres:13

# Set the environment variables for PostgreSQL
ENV POSTGRES_USER=admin
ENV POSTGRES_PASSWORD=password
ENV POSTGRES_DB=mydb

# Expose PostgreSQL port
EXPOSE 5432
###########################################################################
# 9. Alpine Linux
# Use the Alpine image
FROM alpine:3.15

# Install any required packages (e.g., curl, bash)
RUN apk add --no-cache curl bash

# Set the working directory
WORKDIR /app

# Copy application files
COPY . /app

# Expose port if necessary
EXPOSE 8080

# Run a simple command
CMD ["bash"]
###########################################################################
# 10. Ubuntu (20.04)
# Use the Ubuntu 20.04 image
FROM ubuntu:20.04

# Update and install some basic utilities
RUN apt-get update && apt-get install -y \
    curl \
    vim \
    git

# Set the working directory
WORKDIR /app

# Copy the application code
COPY . /app

# Expose port if necessary
EXPOSE 8080

# Run a shell command
CMD ["bash"]
###########################################################################
# 11. MongoDB (4.4)
# Use the MongoDB 4.4 image
FROM mongo:4.4

# Expose MongoDB default port
EXPOSE 27017
###########################################################################
# 12. Elasticsearch (7.10.1)
# Use the Elasticsearch image
FROM docker.elastic.co/elasticsearch/elasticsearch:7.10.1

# Set the environment variables for Elasticsearch
ENV discovery.type=single-node

# Expose the default Elasticsearch ports
EXPOSE 9200 9300
###########################################################################
# 13. Apache HTTP Server (2.4)
# Use the Apache HTTP Server image
FROM httpd:2.4

# Copy custom Apache configuration
COPY httpd.conf /usr/local/apache2/conf/httpd.conf

# Copy website files to the Apache directory
COPY . /usr/local/apache2/htdocs/

# Expose port 80 for HTTP traffic
EXPOSE 80

# Start Apache HTTP Server
CMD ["httpd-foreground"]