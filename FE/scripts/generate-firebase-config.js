import fs from 'fs';
import { loadEnv } from 'vite';

// 현재 실행 환경 (development, production)
const mode = process.env.NODE_ENV || 'development';

// Vite env 불러오기
const env = loadEnv(mode, process.cwd(), '');

const config = {
  apiKey: env.VITE_FIREBASE_API_KEY,
  authDomain: env.VITE_FIREBASE_AUTH_DOMAIN,
  projectId: env.VITE_FIREBASE_PROJECT_ID,
  storageBucket: env.VITE_FIREBASE_STORAGE_BUCKET,
  messagingSenderId: env.VITE_FIREBASE_MESSAGING_SENDER_ID,
  appId: env.VITE_FIREBASE_APP_ID,
};

fs.writeFileSync('public/firebase-config.json', JSON.stringify(config, null, 2));
console.log('✅ Firebase config exported to public/firebase-config.json');
