# Chat Service Application

Spring Boot, WebSocket, Redis를 활용한 실시간 채팅 서비스입니다.

## 🚀 주요 기능 및 개발 진행 현황

### 1. 실시간 채팅 기반 구축 (완료)
- Spring Boot WebSocket (STOMP) 연동
- Redis Pub/Sub을 활용한 분산 환경 메시지 브로드캐스팅 지원
- `ChatController`, `RedisPublisher`, `RedisSubscriber` 구현

### 2. 채팅방 관리 및 고도화 (진행 중)
- **Redis 기반 영속성**: 채팅방 정보를 Redis Hash (`CHAT_ROOM`)에 저장하여 서버 재시작 시에도 유지 및 공유 가능
- **채팅방 상세 조회 및 삭제**: 특정 채팅방 정보 조회 및 삭제 API 구현 (`DELETE /chat/room/{roomId}`)
- **실시간 인원수 관리**: 
  - Redis Set (`USER_COUNT:{roomId}`)을 활용하여 중복 없는 실시간 참여 인원수 관리
  - 채팅방 목록 조회 시 현재 인원수 실시간 반영 (`userCount` 필드 제공)
  - 유저 입장 시(`ENTER`) 인원수 자동 증가 및 메시지에 반영

## 🛠 기술 스택
- **Framework**: Spring Boot 3.x (Kotlin)
- **Messaging**: WebSocket (STOMP), Redis Pub/Sub
- **Storage**: Redis (Data Persistence)
- **Infrastructure**: Docker Compose (Redis 컨테이너 환경)

## 🧪 테스트 방법
1. **Redis 실행**: `docker compose up -d`
2. **애플리케이션 실행**: `./mvnw spring-boot:run`
3. **통합 테스트 실행**: `./mvnw test`
\n\n# CI/CD Test - Fri Feb 27 10:37:18 KST 2026
