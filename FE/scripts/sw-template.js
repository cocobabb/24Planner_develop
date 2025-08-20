import fs from 'fs';
import path from 'path';

const templatePath = path.resolve('public/firebase-messaging-sw.js.template');
const outputPath = path.resolve('public/firebase-messaging-sw.js');

let content = fs.readFileSync(templatePath, 'utf-8');

Object.keys(process.env).forEach((key) => {
  if (key.startsWith('VITE_FIREBASE_')) {
    const regex = new RegExp(`{{${key}}}`, 'g');
    content = content.replace(regex, process.env[key]);
  }
});

fs.writeFileSync(outputPath, content, 'utf-8');
console.log('✅ firebase-messaging-sw.js 생성 완료');
