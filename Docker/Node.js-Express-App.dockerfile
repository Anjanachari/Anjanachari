FROM node:18

LABEL maintainer="you@example.com"
WORKDIR /app

COPY package*.json ./
RUN npm install

COPY . .

ENV PORT=3000
EXPOSE 3000

CMD ["node", "server.js"]
