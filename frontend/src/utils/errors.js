export function getErrorMessage(json) {
    console.log(`[ERR] ${json}`);
    if (Object.prototype.hasOwnProperty.call(json, 'message') &&
        Object.prototype.hasOwnProperty.call(json, 'code')) {
        return `Произошла ошибка ${json.code}: ${json.message}`;
    }
    return 'Произошла ошибка :('
}