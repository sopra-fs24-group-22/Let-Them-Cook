export const formatDateTime = (date: string) => {
  const y = date.slice(0, 4);
  const m = date.slice(5, 7);
  const d = date.slice(8, 10);
  const h = date.slice(11, 13);
  const i = date.slice(14, 16);

  return `${d}.${m}.${y} ${h}:${i}`;
}