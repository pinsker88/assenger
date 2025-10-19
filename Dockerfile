# --- Build stage ---
FROM node:20-alpine AS build
WORKDIR /app


# Install deps first (better caching)
COPY ./webapp/package.json ./webapp/package-lock.json* ./
RUN npm ci || npm i


# Copy source
COPY ./webapp .


# Build for production
RUN npm run build


# --- Runtime stage ---
FROM nginx:1.27-alpine


# Nginx config
COPY ./webapp/nginx.conf /etc/nginx/conf.d/default.conf


# Static site
COPY --from=build /app/dist /usr/share/nginx/html


# Healthcheck (simple)
HEALTHCHECK --interval=30s --timeout=3s CMD wget -qO- http://localhost/ || exit 1


EXPOSE 80