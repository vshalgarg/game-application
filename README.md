# Chief Drop

Single-player drop game.

## Runtime

- Node.js 22 LTS or 24 LTS. Node 22 is the version in `.nvmrc`. Node 26 (Current) is not a target.
- React 19.3, the current stable release. React does not publish a separate LTS line.
- TypeScript 6.0, the newest line the ESLint toolchain supports.
- Phaser 3.90.

## Scripts

```bash
npm install
npm run dev
npm run lint
npm run format:check
npm test
npm run test:coverage
npm run build
```

`npm run test:coverage` writes `coverage/lcov.info` for a SonarQube scan. `sonar-project.properties` points at that file. This repo does not run a SonarQube server.
