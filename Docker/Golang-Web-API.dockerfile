# Stage 1: Build Go binary
FROM golang:1.21 AS build
WORKDIR /src
COPY . .
RUN go build -o app main.go

# Stage 2: Slim runtime image
FROM debian:bullseye-slim
LABEL maintainer="you@example.com"
WORKDIR /app
COPY --from=build /src/app .

EXPOSE 8080
CMD ["./app"]
