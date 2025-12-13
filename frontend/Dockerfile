FROM node:24-alpine AS base

WORKDIR /app

RUN apk add --no-cache bash libc6-compat

COPY package.json package-lock.json* ./

RUN npm install

COPY . .

# ---------------------------
# Target: sviluppo (Expo dev)
# ---------------------------
FROM base AS dev

ENV NODE_ENV=development
ENV HOST=0.0.0.0
ENV CI=1

EXPOSE 8081

CMD ["npx", "expo", "start", "--web", "--port", "8081"]

# ---------------------------
# Target: build web (production)
# ---------------------------
FROM base AS build

ENV NODE_ENV=production
ENV CI=1

RUN npx expo export --platform web --output-dir dist

# ---------------------------
# Target: prod (Nginx + static)
# ---------------------------
FROM nginx:1.27-alpine AS prod

COPY --from=build /app/dist /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
