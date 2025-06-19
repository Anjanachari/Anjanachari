# Base image
FROM python:3.11-slim

# Metadata
LABEL maintainer="you@example.com"
LABEL version="1.0"
LABEL description="Flask API Example"

# Set working directory
WORKDIR /app

# Install dependencies
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Copy source code
COPY . .

# Environment variables
ENV FLASK_APP=app.py
ENV FLASK_RUN_HOST=0.0.0.0

# Expose port
EXPOSE 5000

# Command to run the app
CMD ["flask", "run"]
