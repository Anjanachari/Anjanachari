# Use an official Maven image to build the project
FROM maven:3.8.6-openjdk-11 AS builder

# Set the working directory
WORKDIR /app

# Copy the Maven project files
COPY pom.xml .
COPY src /app/src

# Package the application
RUN mvn clean package

# Use an official Tomcat image to deploy the WAR file
FROM tomcat:10-jdk17

# Copy the WAR file into the webapps directory of Tomcat
COPY --from=builder /app/target/basic-webapp.war /usr/local/tomcat/webapps/

# Expose Tomcat port
EXPOSE 8080

# The default command to run Tomcat
CMD ["catalina.sh", "run"]


#####################################################
# Use an official Maven image to build the project
FROM maven:3.8.6-openjdk-11 AS builder

# Set the working directory
WORKDIR /app

# Copy only the pom.xml and download dependencies to leverage Docker cache
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the rest of the application source code
COPY src /app/src

# Package the application
RUN mvn clean package

# Use an Alpine-based Jetty image to deploy the WAR file
FROM jetty:11-jdk17-alpine

# Copy the WAR file into the webapps directory of Jetty
COPY --from=builder /app/target/basic-webapp.war /var/lib/jetty/webapps/

# Expose Jetty port
EXPOSE 8080
