# Use a minimal base image
FROM alpine:latest

# Install coreutils for df command if not included by default
RUN apk add --no-cache coreutils

# Run df command and save output to /output/df_output.txt
RUN mkdir /output && df > /output/df_output.txt

# Set working directory to /output
WORKDIR /output

# Default command to keep container running for inspection
CMD ["tail", "-f", "/dev/null"]
