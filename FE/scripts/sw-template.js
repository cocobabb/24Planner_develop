import fs from 'fs';
import path from 'path';
import { loadEnv } from 'vite';

const generateSW = () => {
  const mode = process.env.NODE_ENV || 'development';
  const env = loadEnv(mode, process.cwd(), 'VITE_FIREBASE_');

  const templatePath = path.resolve('public/firebase-messaging-sw.js.template');
  const outputPath = path.resolve('public/firebase-messaging-sw.js');

  let content = fs.readFileSync(templatePath, 'utf-8');

  Object.keys(env).forEach((key) => {
    const regex = new RegExp(`{{${key}}}`, 'g');
    content = content.replace(regex, env[key]);
  });

  fs.writeFileSync(outputPath, content, 'utf-8');
  console.log(`✅ firebase-messaging-sw.js 생성 완료 (${mode} 모드)`);
};

generateSW();
