FROM node:alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm install --production
COPY . .
FROM node:alpine

WORKDIR /app
COPY --from=builder /app/node_modules ./node_modules
COPY --from=builder /app/public ./public
COPY --from=builder /app/src ./src
COPY --from=builder /app/index.html ./
COPY --from=builder /app/vite.config.js ./
COPY --from=builder /app/package.json ./
EXPOSE 5173
CMD ["npm", "run", "dev"]