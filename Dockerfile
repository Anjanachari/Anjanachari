# Start from the BusyBox base image
FROM busybox:1.35.0

# Set environment variable
ENV GREETING="Hello from BusyBox"

# Create a directory inside the container
RUN mkdir -p /app

# Add a shell script to the container
COPY hello.sh /app/hello.sh

# Give execute permission
RUN chmod +x /app/hello.sh

# Set the default command
CMD ["/app/hello.sh"]
