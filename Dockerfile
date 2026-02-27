# 1. Frontend Build Stage
FROM node:20 AS frontend-build
WORKDIR /app/frontend
COPY frontend/package*.json ./
RUN npm install
COPY frontend/ ./
RUN npm run build

# 2. Backend Build Stage
FROM eclipse-temurin:21-jdk AS backend-build
WORKDIR /app
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
# 권한 부여 및 의존성 다운로드
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline

COPY src/ src/
# 프론트엔드 빌드 결과물을 스프링 정적 리소스 폴더로 복사
COPY --from=frontend-build /app/frontend/dist/ src/main/resources/static/
RUN ./mvnw package -DskipTests

# 3. Final Runtime Stage
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=backend-build /app/target/*.jar app.jar

# 이미지 내부에서 업로드 폴더 생성
RUN mkdir -p /app/uploads

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
