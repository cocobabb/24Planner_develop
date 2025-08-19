// src/firebase.js
import { initializeApp } from 'firebase/app';
import { getMessaging, getToken, onMessage } from 'firebase/messaging';

// JSON 파일 fetch해서 config 로드
const configResponse = await fetch('/firebase-config.json');
const firebaseConfig = await configResponse.json();

const app = initializeApp(firebaseConfig);
const messaging = getMessaging(app);

export { messaging, getToken, onMessage };
