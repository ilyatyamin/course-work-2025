import {toast} from "react-toastify";

export const Jobs = {REPORT: "отчета", REPORT_FILE: "файла с отчетом", SHEETS_LOADING: "отчета в гугл таблицы"}
export const FinishType = {INFO: 'info', SUCCESS: 'success', ERROR: 'error', WARNING: 'warning', DEFAULT: 'default'};

export function startLoading(job) {
    return toast.loading(`Начали загрузку ${job}... Подождите немного`);
}

export function finishLoading(jobId,
                              type = FinishType.SUCCESS,
                              renderMessage = "Все загрузили!"
) {
    toast.update(jobId, {render: renderMessage, type: type, isLoading: false, autoClose: 5000})
}