import { spawnSync } from 'node:child_process';

export default function swTemplatePlugin() {
  return {
    name: 'sw-template-plugin',
    configureServer() {
      // dev(개발) 서버 시작 시 SW 생성
      spawnSync('node', ['scripts/sw-template.js'], { stdio: 'inherit' });
    },
    buildStart() {
      // 빌드(배포) 시작 시 SW 생성
      spawnSync('node', ['scripts/sw-template.js'], { stdio: 'inherit' });
    },
  };
}
