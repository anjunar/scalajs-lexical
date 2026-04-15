import { defineConfig } from "vite";
import scalaJSPlugin from "@scala-js/vite-plugin-scalajs";
import { resolve } from "node:path"

export default defineConfig({
    base: "./",
    root: "application/src/main/webapp/",
    build: {
        outDir: resolve(__dirname, "docs"),
        emptyOutDir: true,
    },
    plugins: [
        scalaJSPlugin(),
    ],
    resolve: {
        alias: {
            "@lexical-css": resolve(__dirname, "./library/src/main/resources/lexical/index.css")
        }
    }
});