# Build stage
FROM node:20-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

# Run stage
FROM node:20-alpine
WORKDIR /app
COPY --from=build /app/.output ./.output

EXPOSE 3000
# Nuxt 3'ü standalone modda çalıştırıyoruz
CMD ["node", ".output/server/index.mjs"]
