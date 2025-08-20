import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import compression from 'vite-plugin-compression';
import tailwindcss from '@tailwindcss/vite';
import swTemplatePlugin from './vite-plugin-sw-template';

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    react(),
    tailwindcss(),
    swTemplatePlugin(), // 통합 SW 플러그인
    compression({
      algorithm: 'gzip',
      ext: '.gz',
      threshold: 1024,
      deleteOriginFile: false,
    }),
  ],
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        ws: true,
        secure: false,
      },
    },
  },
  define: {
    global: 'window',
  },
});
