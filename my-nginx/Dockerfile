# Use the official NGINX base image
FROM nginx:latest

# Copy custom static files into the default nginx directory
COPY html/ /usr/share/nginx/html/

# Optional: Expose port (useful for documentation, not mandatory)
EXPOSE 80

# No need to override CMD unless you're modifying NGINX behavior