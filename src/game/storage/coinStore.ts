import { gameConfig } from "../../config/gameConfig";
import type { KeyValueStore } from "../../logic/coins";

export const coinStore: KeyValueStore = createCoinStore();

function createCoinStore(): KeyValueStore {
  if (typeof localStorage === "undefined" || !canUseStorage()) {
    return memoryStore();
  }
  return {
    getItem(key: string): string | null {
      return localStorage.getItem(key);
    },
    setItem(key: string, value: string): void {
      localStorage.setItem(key, value);
    },
  };
}

function canUseStorage(): boolean {
  try {
    const probe = `${gameConfig.coinStorageKey}.probe`;
    localStorage.setItem(probe, "ok");
    localStorage.removeItem(probe);
    return true;
  } catch {
    return false;
  }
}

function memoryStore(): KeyValueStore {
  const items = new Map<string, string>();
  return {
    getItem(key: string): string | null {
      return items.get(key) ?? null;
    },
    setItem(key: string, value: string): void {
      items.set(key, value);
    },
  };
}
