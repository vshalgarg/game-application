const LEVELS = {
  debug: 0,
  info: 1,
  warn: 2,
  error: 3,
  silent: 4,
};

const normalizeLevel = (value) => {
  const level = String(value ?? 'warn').toLowerCase();
  return Object.hasOwn(LEVELS, level) ? level : 'warn';
};

const currentLevel = normalizeLevel(import.meta.env.VITE_LOG_LEVEL);

const shouldLog = (level) => LEVELS[level] >= LEVELS[currentLevel];

export const logger = {
  debug: (...args) => {
    if (shouldLog('debug')) console.debug(...args);
  },
  info: (...args) => {
    if (shouldLog('info')) console.info(...args);
  },
  warn: (...args) => {
    if (shouldLog('warn')) console.warn(...args);
  },
  error: (...args) => {
    if (shouldLog('error')) console.error(...args);
  },
};
