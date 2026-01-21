export function uuidV4(): string {
  const c = globalThis.crypto as Crypto | undefined;
  if (c?.randomUUID) return c.randomUUID();

  const bytes = new Uint8Array(16);
  (c?.getRandomValues ? c.getRandomValues(bytes) : bytes.fill(Math.floor(Math.random() * 256)));

  bytes[6] = (bytes[6] & 0x0f) | 0x40;
  bytes[8] = (bytes[8] & 0x3f) | 0x80;

  const toHex = (n: number) => n.toString(16).padStart(2, "0");
  const hex = Array.from(bytes).map(toHex).join("");

  return `${hex.slice(0, 8)}-${hex.slice(8, 12)}-${hex.slice(12, 16)}-${hex.slice(16, 20)}-${hex.slice(20)}`;
}
