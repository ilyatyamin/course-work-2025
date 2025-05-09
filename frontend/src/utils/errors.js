import {toast} from "react-toastify";
import {formatDateForBackend} from "./datetimeTools.js";

export function alertMessage(msg) {
    toast.error(msg);
}

export async function handleBusinessError(err) {
    let message = await err.json();
    let now = formatDateForBackend(new Date());
    console.log(`[ERR][${now}] ${message.message}. msg_code = ${message.code}, http = ${err.code}`);

    toast.error(getErrorMessage(message));
}

function getErrorMessage(json) {
    if (Object.prototype.hasOwnProperty.call(json, 'message') &&
        Object.prototype.hasOwnProperty.call(json, 'code')) {
        return `Произошла ошибка. ${json.message} (${json.code})`;
    }
    return 'Произошла ошибка :('
}