export function getDate(date) {
  return date.toISOString().slice(0, 10);
}

export function isValidDate(date) {
  return date instanceof Date && !isNaN(date);
}
