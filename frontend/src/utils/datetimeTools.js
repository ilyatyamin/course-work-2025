export function formatDateForBackend(dateStr) {
    if (!dateStr) {
        return null;
    }
    const date = new Date(dateStr);
    return `${date.getFullYear()}-${(date.getMonth() + 1)
        .toString()
        .padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(
        date.getHours()
    ).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}:${String(
        date.getSeconds()
    ).padStart(2, '0')}`
}