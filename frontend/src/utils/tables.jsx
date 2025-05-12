import React from 'react';

function DataTable({ data, transpose = false, onDelete = null }) {
    if (!data || Object.keys(data).length === 0) {
        return <p className="text-gray-500">Нет данных для отображения.</p>;
    }

    const rowEntries = Object.entries(data);
    const colKeys = Object.keys(rowEntries[0][1]);

    if (transpose) {
        return (
            <div className="overflow-x-auto">
                <table className="min-w-full border border-gray-300">
                    <thead className="bg-gray-50">
                    <tr>
                        <th className="py-2 px-4 border-b font-semibold text-left">Поле</th>
                        {rowEntries.map(([id]) => (
                            <th key={id} className="py-2 px-4 border-b font-semibold text-left">
                                {id}
                            </th>
                        ))}
                    </tr>
                    </thead>
                    <tbody>
                    {colKeys.map((key, i) => (
                        <tr key={i} className="hover:bg-gray-100">
                            <td className="py-2 px-4 border-b font-semibold">{key}</td>
                            {rowEntries.map(([id, rowData]) => (
                                <td key={`${id}-${key}`} className="py-2 px-4 border-b text-sm text-gray-700">
                                    {rowData[key]}
                                </td>
                            ))}
                        </tr>
                    ))}
                    </tbody>
                </table>
            </div>
        );
    }

    // Обычный режим (без transpose)
    return (
        <div className="overflow-x-auto">
            <table className="min-w-full border border-gray-300">
                <thead className="bg-gray-50">
                <tr>
                    <th className="py-2 px-4 border-b font-semibold text-left">ID</th>
                    {colKeys.map((key, i) => (
                        <th key={i} className="py-2 px-4 border-b font-semibold text-left">
                            {key}
                        </th>
                    ))}
                    {onDelete && (
                        <th className="py-2 px-4 border-b font-semibold text-left">Удалить</th>
                    )}
                </tr>
                </thead>
                <tbody>
                {rowEntries.map(([id, rowData]) => (
                    <tr key={id} className="hover:bg-gray-100">
                        <td className="py-2 px-4 border-b font-semibold">{id}</td>
                        {Object.values(rowData).map((val, i) => (
                            <td key={`${id}-${i}`} className="py-2 px-4 border-b text-sm text-gray-700">
                                {val}
                            </td>
                        ))}
                        {onDelete && (
                            <td className="py-2 px-4 border-b">
                                <button
                                    onClick={() => onDelete(id)}
                                    className="text-red-500 hover:underline text-sm"
                                >
                                    Удалить
                                </button>
                            </td>
                        )}
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
}

export default DataTable;
