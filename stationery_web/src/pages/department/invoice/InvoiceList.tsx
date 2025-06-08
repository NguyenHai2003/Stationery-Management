import { useState } from 'react'
import { FaSearch } from 'react-icons/fa'

export type Invoice = {
  id: string
  department: string
  amount: number
  date: string
  pdfUrl: string
}

interface InvoiceListProps {
  invoices: Invoice[]
  selectedYear: string
  selectedMonth: string
  onSelectInvoice: (pdfUrl: string) => void
}

const ITEMS_PER_PAGE = 5

const InvoiceList: React.FC<InvoiceListProps> = ({ invoices, selectedYear, selectedMonth, onSelectInvoice }) => {
  const [search, setSearch] = useState('')
  const [page, setPage] = useState(1)

  // Lọc hóa đơn theo năm, tháng và ID
  const filteredInvoices = invoices.filter((invoice) => {
    const year = invoice.date.slice(0, 4)
    const month = invoice.date.slice(5, 7)
    return (
      selectedYear === year &&
      (selectedMonth === 'All' || month === selectedMonth) &&
      invoice.id.toLowerCase().includes(search.toLowerCase())
    )
  })

  // Phân trang
  const totalPages = Math.ceil(filteredInvoices.length / ITEMS_PER_PAGE)
  const paginatedInvoices = filteredInvoices.slice((page - 1) * ITEMS_PER_PAGE, page * ITEMS_PER_PAGE)

  return (
    <div className='bg-white rounded-xl shadow-lg p-8'>
      <h2 className='text-2xl font-semibold text-gray-800 mb-6'>Danh Sách Hóa Đơn</h2>
      {/* Tìm kiếm */}
      <div className='mb-6 flex flex-col sm:flex-row gap-4'>
        <div className='relative flex-1'>
          <FaSearch className='absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400' />
          <input
            type='text'
            placeholder='Tìm kiếm theo ID'
            value={search}
            onChange={(e) => {
              setSearch(e.target.value)
              setPage(1)
            }}
            className='pl-10 p-2 border rounded-lg w-full focus:outline-none focus:ring-2 focus:ring-blue-600'
          />
        </div>
      </div>
      {/* Bảng */}
      <div className='overflow-x-auto'>
        <table className='w-full text-left'>
          <thead>
            <tr className='bg-gradient-to-r from-blue-50 to-blue-100 text-blue-800'>
              <th className='p-4 text-sm font-semibold'>Mã hóa đơn</th>
              <th className='p-4 text-sm font-semibold'>Tổng tiền</th>
              <th className='p-4 text-sm font-semibold'>Ngày</th>
              <th className='p-4 text-sm font-semibold'>Hành động</th>
            </tr>
          </thead>
          <tbody>
            {paginatedInvoices.length > 0 ? (
              paginatedInvoices.map((invoice) => (
                <tr key={invoice.id} className='border-b hover:bg-blue-50/50 transition'>
                  <td className='p-4 text-sm text-gray-700'>{invoice.id}</td>
                  <td className='p-4 text-sm text-gray-700'>${invoice.amount.toFixed(2)}</td>
                  <td className='p-4 text-sm text-gray-700'>{invoice.date}</td>
                  <td className='p-4 text-sm'>
                    <button
                      onClick={() => onSelectInvoice(invoice.pdfUrl)}
                      className='px-3 py-1 bg-gradient-to-r from-blue-600 to-blue-700 text-white rounded-lg hover:from-blue-700 hover:to-blue-800 transition'
                    >
                      Xem PDF
                    </button>
                  </td>
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan={4} className='p-4 text-center text-gray-500'>
                  Không tìm thấy hóa đơn.
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
      {/* Phân trang */}
      {totalPages > 1 && (
        <div className='mt-6 flex justify-between items-center'>
          <button
            onClick={() => setPage((p) => Math.max(p - 1, 1))}
            disabled={page === 1}
            className='px-4 py-2 bg-gray-200 rounded-lg text-gray-700 hover:bg-gray-300 disabled:opacity-50 transition'
          >
            Trước
          </button>
          <span className='text-sm text-gray-700'>
            Trang {page} / {totalPages}
          </span>
          <button
            onClick={() => setPage((p) => Math.min(p + 1, totalPages))}
            disabled={page === totalPages}
            className='px-4 py-2 bg-gray-200 rounded-lg text-gray-700 hover:bg-gray-300 disabled:opacity-50 transition'
          >
            Tiếp
          </button>
        </div>
      )}
    </div>
  )
}

export default InvoiceList
