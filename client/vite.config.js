import { defineConfig } from "vite";
import vue from "@vitejs/plugin-vue";
import path from "path";
import prerender from "vite-plugin-prerender";

export default defineConfig(({ command }) => ({
  plugins: [
    vue(),
    // Prerender apex marketing routes only during build for better SEO
    ...(command === "build"
      ? [
          prerender({
            staticDir: path.resolve(__dirname, "dist"),
        routes: ["/", "/about", "/qr-menu"],
          }),
        ]
      : []),
  ],
  resolve: {
    alias: {
      "@": path.resolve(__dirname, "src"),
    },
  },
  server: {
    host: true, // allow access via subdomains like my-bistro.localhost
    proxy: {
      "/api": {
        target: "http://localhost:8080",
        // Keep original Host (e.g., my-bistro.localhost) so backend can resolve tenant from subdomain
        changeOrigin: false,
        rewrite: undefined,
      },
      "/files": {
        target: "http://localhost:8080",
        changeOrigin: false,
      },
      // Backwards-compat: if any old URLs point to /uploads, rewrite to /files
      "/uploads": {
        target: "http://localhost:8080",
        changeOrigin: false,
        rewrite: (path) => path.replace(/^\/uploads/, "/files"),
      },
    },
  },
}));
