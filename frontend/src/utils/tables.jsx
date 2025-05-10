import React from 'react';

function DataTable({ data, transpose = false }) {
    if (!data || Object.keys(data).length === 0) {
        return <p className="text-gray-500">Нет данных для отображения.</p>;
    }

    const rowEntries = Object.entries(data);
    const colKeys = Object.keys(rowEntries[0][1]);

    return (
        <div className="overflow-x-auto">
            <table className="min-w-full border border-gray-300">
                <thead className="bg-gray-50">
                <tr>
                    <th className="py-2 px-4 border-b font-semibold text-left">

                    </th>
                    {(transpose ? rowEntries : colKeys).map(([key] = '', i) => (
                        <th
                            key={i}
                            className="py-2 px-4 border-b font-semibold text-left"
                        >
                            {transpose ? key : colKeys[i]}
                        </th>
                    ))}
                </tr>
                </thead>
                <tbody>
                {(transpose ? colKeys : rowEntries).map((rowKey, rowIndex) => {
                    const actualKey = transpose ? rowKey : rowEntries[rowIndex][0];
                    const values = transpose
                        ? rowEntries.map(([, rowData]) => rowData[rowKey])
                        : Object.values(rowEntries[rowIndex][1]);

                    return (
                        <tr key={rowKey} className="hover:bg-gray-100">
                            <td className="py-2 px-4 border-b font-semibold">
                                {actualKey}
                            </td>
                            {values.map((val, i) => (
                                <td
                                    key={`${actualKey}-${i}`}
                                    className="py-2 px-4 border-b text-sm text-gray-700"
                                >
                                    {val}
                                </td>
                            ))}
                        </tr>
                    );
                })}
                </tbody>
            </table>
        </div>
    );
}

export default DataTable;
