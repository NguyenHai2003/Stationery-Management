import { useState } from 'react'
import {
  FaCheck,
  FaTimes,
  FaSearch,
  FaFilter,
  FaEye,
  FaCheckCircle,
  FaTimesCircle,
  FaFileInvoice
} from 'react-icons/fa'
import { FiRefreshCw } from 'react-icons/fi'
import Select from 'react-select'
import Swal from 'sweetalert2'

const OrderManagement = () => {
  // Mock data for orders with createdAt including time
  const [orders, setOrders] = useState([
    {
      id: '1',
      userType: 'Department',
      userName: 'IT Department',
      createdAt: '2025-04-23 14:30:00',
      amount: 1500000,
      status: 'PENDING',
      details: [
        {
          productName: 'Ballpoint Pen',
          quantity: 100,
          price: 5000,
          color: 'Blue',
          size: 'Medium',
          image: 'https://via.placeholder.com/50?text=Pen'
        },
        {
          productName: 'Notebook',
          quantity: 50,
          price: 20000,
          color: 'Black',
          size: 'A5',
          image: 'https://via.placeholder.com/50?text=Notebook'
        }
      ]
    },
    {
      id: '2',
      userType: 'User',
      userName: 'John Doe',
      createdAt: '2025-04-22 09:15:00',
      amount: 500000,
      status: 'PENDING',
      details: [
        {
          productName: 'Pencil',
          quantity: 20,
          price: 25000,
          color: 'Yellow',
          size: 'Standard',
          image: 'https://via.placeholder.com/50?text=Pencil'
        }
      ]
    },
    {
      id: '3',
      userType: 'Department',
      userName: 'Economics Department',
      createdAt: '2025-04-21 16:45:00',
      amount: 2000000,
      status: 'PROCESSING',
      details: [
        {
          productName: 'Casio Calculator',
          quantity: 10,
          price: 200000,
          color: 'Silver',
          size: 'Standard',
          image: 'https://via.placeholder.com/50?text=Calculator'
        }
      ]
    },
    {
      id: '4',
      userType: 'User',
      userName: 'Jane Smith',
      createdAt: '2025-04-20 11:20:00',
      amount: 300000,
      status: 'COMPLETED',
      details: [
        {
          productName: 'Ink Pen',
          quantity: 30,
          price: 10000,
          color: 'Black',
          size: 'Fine',
          image: 'https://via.placeholder.com/50?text=Ink+Pen'
        }
      ]
    }
  ])

  // State for filters of each table
  const [pendingFilterType, setPendingFilterType] = useState('All')
  const [pendingSearchTerm, setPendingSearchTerm] = useState('')
  const [confirmedFilterType, setConfirmedFilterType] = useState('All')
  const [confirmedSearchTerm, setConfirmedSearchTerm] = useState('')
  const [confirmedFilterStatus, setConfirmedFilterStatus] = useState('All')

  // Format date-time
  const formatDateTime = (dateTime) => {
    const date = new Date(dateTime)
    return date
      .toLocaleString('en-GB', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
      })
      .replace(',', '')
  }

  // Sort orders by createdAt in descending order
  const sortOrdersByCreatedAt = (orders) => {
    return [...orders].sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
  }

  // Filter orders by status and customer type
  const pendingOrders = sortOrdersByCreatedAt(
    orders.filter(
      (order) =>
        order.status === 'PENDING' &&
        (pendingFilterType === 'All' || order.userType === pendingFilterType) &&
        (order.userName.toLowerCase().includes(pendingSearchTerm.toLowerCase()) || order.id.includes(pendingSearchTerm))
    )
  )

  const confirmedOrders = sortOrdersByCreatedAt(
    orders.filter(
      (order) =>
        order.status !== 'PENDING' &&
        (confirmedFilterType === 'All' || order.userType === confirmedFilterType) &&
        (confirmedFilterStatus === 'All' || order.status === confirmedFilterStatus) &&
        (order.userName.toLowerCase().includes(confirmedSearchTerm.toLowerCase()) ||
          order.id.includes(confirmedSearchTerm))
    )
  )

  // Status options for react-select with custom colors
  const statusOptions = [
    { value: 'PROCESSING', label: 'Processing', color: '#f59e0b' }, // Yellow
    { value: 'SHIPPING', label: 'Shipping', color: '#3b82f6' }, // Blue
    { value: 'COMPLETED', label: 'Completed', color: '#10b981' }, // Green
    { value: 'CANCELED', label: 'Canceled', color: '#ef4444' } // Red
  ]

  // Handle confirm order
  const handleConfirmOrder = (id) => {
    Swal.fire({
      title: 'Confirm Order?',
      text: 'Are you sure you want to confirm this order?',
      icon: 'question',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: 'Confirm',
      cancelButtonText: 'Cancel'
    }).then((result) => {
      if (result.isConfirmed) {
        setOrders((prev) => prev.map((order) => (order.id === id ? { ...order, status: 'PROCESSING' } : order)))
        Swal.fire('Confirmed!', 'The order has been confirmed.', 'success')
      }
    })
  }

  // Handle cancel order
  const handleCancelOrder = (id) => {
    Swal.fire({
      title: 'Cancel Order?',
      text: 'Are you sure you want to cancel this order?',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      cancelButtonColor: '#3085d6',
      confirmButtonText: 'Cancel Order',
      cancelButtonText: 'Back'
    }).then((result) => {
      if (result.isConfirmed) {
        setOrders((prev) => prev.map((order) => (order.id === id ? { ...order, status: 'CANCELED' } : order)))
        Swal.fire('Canceled!', 'The order has been canceled.', 'success')
      }
    })
  }

  // Handle update status
  const handleUpdateStatus = (id, selectedOption) => {
    const newStatus = selectedOption.value
    Swal.fire({
      title: 'Update Status?',
      text: `Do you want to update the status to "${selectedOption.label}"?`,
      icon: 'question',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: 'Update',
      cancelButtonText: 'Cancel'
    }).then((result) => {
      if (result.isConfirmed) {
        setOrders((prev) => prev.map((order) => (order.id === id ? { ...order, status: newStatus } : order)))
        Swal.fire('Updated!', 'The order status has been updated.', 'success')
      }
    })
  }

  const handleViewInvoice = (order) => {
    // Logic để hiển thị hóa đơn
    // Bạn có thể tạo URL hoặc nội dung hóa đơn dựa trên order
    console.log('Viewing invoice for order:', order.id)
    // Ví dụ: window.open(`/invoices/${order.id}`, '_blank');
  }

  const handleResendInvoice = (orderId) => {
    // Logic gửi lại hóa đơn
    console.log('Resending invoice for order:', orderId)
    // Gọi API để gửi lại email
    // api.resendInvoice(orderId).then(...);
  }

  // Handle view order details
  const handleViewDetails = (details) => {
    const detailContent = details
      .map(
        (detail) =>
          `<tr>
            <td class="border px-4 py-2"><img src="${detail.image}" alt="${detail.productName}" class="w-12 h-12 object-cover" /></td>
            <td class="border px-4 py-2">${detail.productName}</td>
            <td class="border px-4 py-2">${detail.quantity}</td>
            <td class="border px-4 py-2">${detail.price.toLocaleString('vi-VN')} VND</td>
            <td class="border px-4 py-2">${detail.color}</td>
            <td class="border px-4 py-2">${detail.size}</td>
          </tr>`
      )
      .join('')

    Swal.fire({
      title: 'Order Details',
      html: `
        <table class="w-full border-collapse border">
          <thead>
            <tr class="bg-blue-600 text-white">
              <th class="border px-4 py-2">Image</th>
              <th class="border px-4 py-2">Product Name</th>
              <th class="border px-4 py-2">Quantity</th>
              <th class="border px-4 py-2">Price</th>
              <th class="border px-4 py-2">Color</th>
              <th class="border px-4 py-2">Size</th>
            </tr>
          </thead>
          <tbody>
            ${detailContent}
          </tbody>
        </table>
      `,
      showConfirmButton: true,
      confirmButtonText: 'Close',
      width: '800px'
    })
  }

  // Custom styles for react-select
  const customStyles = {
    control: (provided) => ({
      ...provided,
      borderRadius: '0.375rem',
      borderColor: '#d1d5db',
      boxShadow: 'none',
      '&:hover': { borderColor: '#2563eb' }
    }),
    menu: (provided) => ({
      ...provided,
      zIndex: 99999
    }),
    menuPortal: (provided) => ({
      ...provided,
      zIndex: 99999
    }),
    option: (provided, state) => ({
      ...provided,
      backgroundColor: state.isSelected ? state.data.color : state.isFocused ? `${state.data.color}33` : 'white',
      color: state.isSelected ? 'white' : 'black',
      '&:hover': {
        backgroundColor: `${state.data.color}33`
      }
    }),
    singleValue: (provided, state) => ({
      ...provided,
      color: state.data.color
    })
  }

  return (
    <div className='p-6 w-full mx-auto bg-white shadow-lg rounded-xl'>
      {/* Header Section */}
      <div className='flex justify-between items-center mb-6'>
        <h1 className='text-3xl font-semibold text-blue-800'>Order Management</h1>
      </div>

      {/* Table 1: Pending Orders */}
      <div className='mb-10'>
        <h2 className='text-2xl font-semibold text-blue-700 mb-4'>Pending Orders</h2>
        {/* Filters for Table 1 */}
        <div className='flex gap-4 mb-6'>
          <div className='relative w-1/3'>
            <span className='absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none text-gray-400'>
              <FaSearch />
            </span>
            <input
              type='text'
              placeholder='Search by order ID or customer name...'
              className='pl-10 pr-4 py-2 w-full border border-gray-300 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-300'
              value={pendingSearchTerm}
              onChange={(e) => setPendingSearchTerm(e.target.value)}
            />
          </div>
          <div className='flex items-center gap-2'>
            <FaFilter className='text-gray-400' />
            <select
              className='px-4 py-2 border border-gray-300 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-300'
              value={pendingFilterType}
              onChange={(e) => setPendingFilterType(e.target.value)}
            >
              <option value='All'>All</option>
              <option value='Department'>Department</option>
              <option value='User'>User</option>
            </select>
          </div>
        </div>
        <div className='overflow-x-auto rounded-xl shadow-lg'>
          <table className='w-full border-collapse border border-blue-200'>
            <thead>
              <tr className='bg-blue-600 text-white text-left'>
                <th className='px-4 py-3 font-medium text-sm uppercase tracking-wider'>Order ID</th>
                <th className='px-4 py-3 font-medium text-sm uppercase tracking-wider'>Customer Type</th>
                <th className='px-4 py-3 font-medium text-sm uppercase tracking-wider'>Customer Name</th>
                <th className='px-4 py-3 font-medium text-sm uppercase tracking-wider'>Created At</th>
                <th className='px-4 py-3 font-medium text-sm uppercase tracking-wider'>Total Amount</th>
                <th className='px-4 py-3 font-medium text-sm uppercase tracking-wider'>Actions</th>
              </tr>
            </thead>
            <tbody>
              {pendingOrders.map((order) => (
                <tr key={order.id} className='border-b border-blue-200 hover:bg-blue-50 transition-colors'>
                  <td className='px-4 py-3'>{order.id}</td>
                  <td className='px-4 py-3'>{order.userType}</td>
                  <td className='px-4 py-3 font-medium'>{order.userName}</td>
                  <td className='px-4 py-3'>{formatDateTime(order.createdAt)}</td>
                  <td className='px-4 py-3'>{order.amount.toLocaleString('vi-VN')} VND</td>
                  <td className='px-4 py-3 flex gap-2'>
                    <button
                      className='bg-green-500 text-white p-2 rounded-lg hover:bg-green-600 transition-colors'
                      onClick={() => handleConfirmOrder(order.id)}
                      title='Confirm'
                    >
                      <FaCheck size={16} />
                    </button>
                    <button
                      className='bg-red-500 text-white p-2 rounded-lg hover:bg-red-600 transition-colors'
                      onClick={() => handleCancelOrder(order.id)}
                      title='Cancel'
                    >
                      <FaTimes size={16} />
                    </button>
                    <button
                      className='bg-blue-500 text-white p-2 rounded-lg hover:bg-blue-600 transition-colors'
                      onClick={() => handleViewDetails(order.details)}
                      title='View Details'
                    >
                      <FaEye size={16} />
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* Table 2: Confirmed Orders */}
      <div>
        <h2 className='text-2xl font-semibold text-blue-700 mb-4'>Confirmed Orders</h2>
        {/* Filters for Table 2 */}
        <div className='flex gap-4 mb-6'>
          <div className='relative w-1/3'>
            <span className='absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none text-gray-400'>
              <FaSearch />
            </span>
            <input
              type='text'
              placeholder='Search by order ID or customer name...'
              className='pl-10 pr-4 py-2 w-full border border-gray-300 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-300'
              value={confirmedSearchTerm}
              onChange={(e) => setConfirmedSearchTerm(e.target.value)}
            />
          </div>
          <div className='flex items-center gap-2'>
            <FaFilter className='text-gray-400' />
            <select
              className='px-4 py-2 border border-gray-300 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-300'
              value={confirmedFilterType}
              onChange={(e) => setConfirmedFilterType(e.target.value)}
            >
              <option value='All'>All</option>
              <option value='Department'>Department</option>
              <option value='User'>User</option>
            </select>
          </div>
          <div className='flex items-center gap-2'>
            <FaFilter className='text-gray-400' />
            <select
              className='px-4 py-2 border border-gray-300 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-300'
              value={confirmedFilterStatus}
              onChange={(e) => setConfirmedFilterStatus(e.target.value)}
            >
              <option value='All'>All</option>
              <option value='PROCESSING'>Processing</option>
              <option value='SHIPPING'>Shipping</option>
              <option value='COMPLETED'>Completed</option>
              <option value='CANCELED'>Canceled</option>
            </select>
          </div>
        </div>
        <div className='overflow-x-auto rounded-xl shadow-lg'>
          <table className='w-full border-collapse border border-blue-200'>
            <thead>
              <tr className='bg-blue-600 text-white text-left'>
                <th className='px-4 py-3 font-medium text-sm uppercase tracking-wider'>Order ID</th>
                <th className='px-4 py-3 font-medium text-sm uppercase tracking-wider'>Customer Type</th>
                <th className='px-4 py-3 font-medium text-sm uppercase tracking-wider'>Customer Name</th>
                <th className='px-4 py-3 font-medium text-sm uppercase tracking-wider'>Created At</th>
                <th className='px-4 py-3 font-medium text-sm uppercase tracking-wider'>Total Amount</th>
                <th className='px-4 py-3 font-medium text-sm uppercase tracking-wider'>Details</th>
                <th className='px-4 py-3 font-medium text-sm uppercase tracking-wider'>Status</th>
                <th className='px-4 py-3 font-medium text-sm uppercase tracking-wider'>Invoice</th>
              </tr>
            </thead>
            <tbody>
              {confirmedOrders.map((order) => (
                <tr key={order.id} className='border-b border-blue-200 hover:bg-blue-50 transition-colors'>
                  <td className='px-4 py-3'>{order.id}</td>
                  <td className='px-4 py-3'>{order.userType}</td>
                  <td className='px-4 py-3 font-medium'>{order.userName}</td>
                  <td className='px-4 py-3'>{formatDateTime(order.createdAt)}</td>
                  <td className='px-4 py-3'>{order.amount.toLocaleString('vi-VN')} VND</td>
                  <td className='px-4 py-3'>
                    <button
                      className='bg-blue-500 text-white p-2 rounded-lg hover:bg-blue-600 transition-colors'
                      onClick={() => handleViewDetails(order.details)}
                      title='View Details'
                    >
                      <FaEye size={16} />
                    </button>
                  </td>
                  <td className='px-4 py-3'>
                    <Select
                      options={statusOptions}
                      value={statusOptions.find((option) => option.value === order.status)}
                      onChange={(selectedOption) => handleUpdateStatus(order.id, selectedOption)}
                      className='w-40'
                      styles={customStyles}
                      menuPlacement='auto'
                      menuPortalTarget={document.body}
                    />
                  </td>
                  <td className='px-4 py-3'>
                    <div className='flex items-center space-x-2'>
                      {/* Kiểm tra nếu order.status === 'COMPLETED' thì coi như đã gửi hóa đơn */}
                      {order.status === 'COMPLETED' ? (
                        <>
                          <span className='text-green-600 flex items-center'>
                            <FaCheckCircle className='mr-1' /> Đã gửi
                          </span>
                          <button
                            className='bg-blue-500 text-white p-1 rounded hover:bg-blue-600 transition-colors'
                            onClick={() => handleViewInvoice(order)}
                            title='View Invoice'
                          >
                            <FaFileInvoice size={16} />
                          </button>
                          {/* Nút gửi lại nếu cần */}
                          <button
                            className='bg-gray-500 text-white p-1 rounded hover:bg-gray-600 transition-colors'
                            onClick={() => handleResendInvoice(order.id)}
                            title='Resend Invoice'
                          >
                            <FiRefreshCw size={16} />
                          </button>
                        </>
                      ) : (
                        <span className='text-gray-500 flex items-center'>
                          <FaTimesCircle className='mr-1' /> Chưa gửi
                        </span>
                      )}
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  )
}

export default OrderManagement
