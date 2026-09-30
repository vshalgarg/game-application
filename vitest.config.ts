import { defineConfig } from "vitest/config";

export default defineConfig({
  test: {
    environment: "node",
    coverage: {
      provider: "v8",
      reporter: ["text", "lcov"],
      include: ["src/config/**/*.ts", "src/logic/**/*.ts", "src/game/fall/flight.ts", "src/game/ui/format.ts"],
      exclude: ["**/*.test.ts"],
    },
  },
});
