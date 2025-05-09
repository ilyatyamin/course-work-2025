import React from 'react';

function DataTable({data}) {
    const names = Object.keys(data.A);
    const columnKeys = Object.keys(data).filter(key => key !== 'total');

    return (
        <div className="overflow-x-auto"> {/* Обертка для горизонтальной прокрутки */}
            <table className="min-w-full border border-gray-300">
                <thead className="bg-gray-50">
                <tr>
                    <th className="py-2 px-4 border-b font-semibold text-left">Имя</th>
                    {columnKeys.map((key) => (
                        <th key={key} className="py-2 px-4 border-b font-semibold text-left">{key}</th>
                    ))}
                </tr>
                </thead>
                <tbody>
                {names.map((name) => (
                    <tr key={name} className="hover:bg-gray-100">
                        <td className="py-2 px-4 border-b">{name}</td>
                        {columnKeys.map((key) => (
                            <td key={`${name}-${key}`} className="py-2 px-4 border-b text-center">{data[key][name]}</td>
                        ))}
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
}

export default DataTable;