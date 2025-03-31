# 베이스 이미지
# 폴더 및 파일 복사
# 프로젝트 빌드 명령어 실행
# 서비스 실행 명령어 -> 컨테이너가 실행될 떄 CMD
FROM node AS build
WORKDIR /app

ARG VITE_KAKAO_JAVASCRIPT_KEY
ENV VITE_KAKAO_JAVASCRIPT_KEY=${VITE_KAKAO_JAVASCRIPT_KEY}

ARG VITE_CHAT_URL
ENV VITE_CHAT_URL=${VITE_CHAT_URL}

# 패키지 설치를 위한 패키지 목록 파일 복사
COPY package.json package-lock.json ./

# 패키지 설치
RUN npm install

# 소스 코드 복사
COPY . .

# 프로젝트 빌드
RUN npm run build

# Run Stage
FROM nginx

COPY nginx.conf /etc/nginx/nginx.conf
COPY ./templates /etc/nginx/templates

# Build Stage에서 빌드 결과물(정적 파일) 복사
COPY --from=build /app/dist /usr/share/nginx/html

# 포트 명시
# 문서화 목적
EXPOSE 80

CMD ["nginx", "-g", "daemon off;"] 