import Select from 'react-select'
import { useState, useEffect } from 'react'
import { FiPercent, FiDollarSign, FiCalendar, FiUsers, FiPackage, FiShoppingCart } from 'react-icons/fi'

interface Promotion {
  promotionId: string
  promoCode: string
  discountType: DiscountType
  discountValue: number
  usageLimit: number
  maxValue: number
  minOrderValue: number
  startDate: string
  endDate: string
  createdAt: string
  voucherType: VoucherType
  userIds?: string[]
}

type DiscountType = 'PERCENTAGE' | 'VALUE'
type VoucherType = 'PRODUCT' | 'USER'

interface User {
  id: string
  name: string
}

interface VoucherModalProps {
  isOpen: boolean
  isEdit: boolean
  promotion?: Promotion
  onClose: () => void
  onSubmit: (promotion: Promotion) => void
  users: User[]
}

const VoucherModal = ({ isOpen, isEdit, promotion, onClose, onSubmit, users }: VoucherModalProps) => {
  const [formData, setFormData] = useState<Promotion>({
    promotionId: promotion?.promotionId || '',
    promoCode: promotion?.promoCode || '',
    discountType: promotion?.discountType || 'PERCENTAGE',
    discountValue: promotion?.discountValue || 0,
    usageLimit: promotion?.usageLimit || 0,
    maxValue: promotion?.maxValue || 0,
    minOrderValue: promotion?.minOrderValue || 0,
    startDate: promotion?.startDate || '',
    endDate: promotion?.endDate || '',
    createdAt: promotion?.createdAt || '',
    voucherType: promotion?.voucherType || 'PRODUCT',
    userIds: promotion?.userIds || []
  })

  const [isForAllUsers, setIsForAllUsers] = useState(!promotion?.userIds || promotion.userIds.length === 0)

  useEffect(() => {
    if (promotion) {
      setFormData(promotion)
      setIsForAllUsers(!promotion.userIds || promotion.userIds.length === 0)
    } else {
      setFormData({
        promotionId: '',
        promoCode: '',
        discountType: 'PERCENTAGE',
        discountValue: 0,
        usageLimit: 0,
        maxValue: 0,
        minOrderValue: 0,
        startDate: '',
        endDate: '',
        createdAt: '',
        voucherType: 'PRODUCT',
        userIds: []
      })
      setIsForAllUsers(true)
    }
  }, [promotion])

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target
    setFormData((prev) => ({
      ...prev,
      [name]: value
    }))
  }

  const handleNumberChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target
    setFormData((prev) => ({
      ...prev,
      [name]: Number(value)
    }))
  }

  const handleUserChange = (selectedOptions: any) => {
    setFormData((prev) => ({
      ...prev,
      userIds: selectedOptions ? selectedOptions.map((option: any) => option.value) : []
    }))
  }

  const handleVoucherTypeChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const voucherType = e.target.value as VoucherType
    setFormData((prev) => ({
      ...prev,
      voucherType,
      userIds: voucherType === 'PRODUCT' ? [] : prev.userIds
    }))
    if (voucherType === 'PRODUCT') {
      setIsForAllUsers(true)
    }
  }

  const handleSubmit = () => {
    const finalData = {
      ...formData,
      userIds: formData.voucherType === 'USER' && isForAllUsers ? [] : formData.userIds
    }
    onSubmit(finalData)
  }

  if (!isOpen) return null

  const userOptions = users.map((user) => ({
    value: user.id,
    label: user.name
  }))

  const selectedUsers = userOptions.filter((option) => formData.userIds?.includes(option.value))

  return (
    <div className='fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4 backdrop-blur-sm'>
      <div className='bg-white rounded-xl p-8 w-full max-w-4xl shadow-2xl border border-gray-100'>
        {/* Header */}
        <div className='flex justify-between items-center mb-6'>
          <h2 className='text-3xl font-bold text-gradient bg-gradient-to-r from-blue-600 to-purple-600 bg-clip-text text-transparent'>
            {isEdit ? 'Edit Voucher' : 'Create New Voucher'}
          </h2>
          <button onClick={onClose} className='text-gray-400 hover:text-gray-600 text-2xl' aria-label='Close modal'>
            &times;
          </button>
        </div>

        <div className='grid grid-cols-1 lg:grid-cols-2 gap-8'>
          {/* Left Column */}
          <div className='space-y-6'>
            {/* Voucher Code */}
            <div className='bg-blue-50 p-5 rounded-xl border border-blue-100'>
              <label className='block text-lg font-semibold text-blue-800 mb-3 flex items-center'>
                <FiPackage className='mr-3 text-xl' /> Voucher Code
              </label>
              <input
                type='text'
                name='promoCode'
                value={formData.promoCode}
                onChange={handleChange}
                className='w-full border border-blue-200 rounded-lg p-3 text-base focus:outline-none focus:ring-2 focus:ring-blue-300 focus:border-transparent bg-white'
                placeholder='SUMMER2023'
              />
            </div>

            {/* Voucher Type */}
            <div className='bg-indigo-50 p-5 rounded-xl border border-indigo-100'>
              <label className='block text-lg font-semibold text-indigo-800 mb-3 flex items-center'>
                <FiUsers className='mr-3 text-xl' /> Voucher Type
              </label>
              <select
                name='voucherType'
                value={formData.voucherType}
                onChange={handleVoucherTypeChange}
                className='w-full border border-indigo-200 rounded-lg p-3 text-base focus:outline-none focus:ring-2 focus:ring-indigo-300 focus:border-transparent bg-white'
              >
                <option value='PRODUCT'>Product Voucher (All Products)</option>
                <option value='USER'>User Voucher</option>
              </select>
            </div>

            {/* Discount Settings */}
            <div className='bg-purple-50 p-5 rounded-xl border border-purple-100'>
              <h3 className='text-lg font-semibold text-purple-800 mb-4 flex items-center'>
                {formData.discountType === 'PERCENTAGE' ? (
                  <FiPercent className='mr-3 text-xl' />
                ) : (
                  <FiDollarSign className='mr-3 text-xl' />
                )}
                Discount Settings
              </h3>

              <div className='mb-4'>
                <label className='block text-sm font-medium text-purple-600 mb-2'>Discount Type</label>
                <select
                  name='discountType'
                  value={formData.discountType}
                  onChange={handleChange}
                  className='w-full border border-purple-200 rounded-lg p-3 text-base focus:outline-none focus:ring-2 focus:ring-purple-300 focus:border-transparent bg-white'
                >
                  <option value='PERCENTAGE'>Percentage</option>
                  <option value='VALUE'>Fixed Amount</option>
                </select>
              </div>

              <div className='grid grid-cols-2 gap-4'>
                <div>
                  <label className='block text-sm font-medium text-purple-600 mb-2'>Discount Value</label>
                  <input
                    type='number'
                    name='discountValue'
                    value={formData.discountValue}
                    onChange={handleNumberChange}
                    className='w-full border border-purple-200 rounded-lg p-3 text-base focus:outline-none focus:ring-2 focus:ring-purple-300 focus:border-transparent bg-white'
                    placeholder={formData.discountType === 'PERCENTAGE' ? '10%' : '$20'}
                  />
                </div>

                <div>
                  <label className='block text-sm font-medium text-purple-600 mb-2'>Max Value</label>
                  <input
                    type='number'
                    name='maxValue'
                    value={formData.maxValue}
                    onChange={handleNumberChange}
                    className='w-full border border-purple-200 rounded-lg p-3 text-base focus:outline-none focus:ring-2 focus:ring-purple-300 focus:border-transparent bg-white'
                    placeholder='$50'
                  />
                </div>
              </div>
            </div>
          </div>

          {/* Right Column */}
          <div className='space-y-6'>
            {/* Usage Limits */}
            <div className='bg-teal-50 p-5 rounded-xl border border-teal-100'>
              <h3 className='text-lg font-semibold text-teal-800 mb-4 flex items-center'>
                <FiShoppingCart className='mr-3 text-xl' /> Usage Limits
              </h3>

              <div className='mb-4'>
                <label className='block text-sm font-medium text-teal-600 mb-2'>Usage Limit</label>
                <input
                  type='number'
                  name='usageLimit'
                  value={formData.usageLimit}
                  onChange={handleNumberChange}
                  className='w-full border border-teal-200 rounded-lg p-3 text-base focus:outline-none focus:ring-2 focus:ring-teal-300 focus:border-transparent bg-white'
                  placeholder='100'
                />
              </div>

              <div>
                <label className='block text-sm font-medium text-teal-600 mb-2'>Min Order Value</label>
                <input
                  type='number'
                  name='minOrderValue'
                  value={formData.minOrderValue}
                  onChange={handleNumberChange}
                  className='w-full border border-teal-200 rounded-lg p-3 text-base focus:outline-none focus:ring-2 focus:ring-teal-300 focus:border-transparent bg-white'
                  placeholder='$20'
                />
              </div>
            </div>

            {/* Date Range */}
            <div className='bg-amber-50 p-5 rounded-xl border border-amber-100'>
              <h3 className='text-lg font-semibold text-amber-800 mb-4 flex items-center'>
                <FiCalendar className='mr-3 text-xl' /> Active Period
              </h3>

              <div className='grid grid-cols-2 gap-4'>
                <div>
                  <label className='block text-sm font-medium text-amber-600 mb-2'>Start Date</label>
                  <input
                    type='date'
                    name='startDate'
                    value={formData.startDate}
                    onChange={handleChange}
                    className='w-full border border-amber-200 rounded-lg p-3 text-base focus:outline-none focus:ring-2 focus:ring-amber-300 focus:border-transparent bg-white'
                  />
                </div>

                <div>
                  <label className='block text-sm font-medium text-amber-600 mb-2'>End Date</label>
                  <input
                    type='date'
                    name='endDate'
                    value={formData.endDate}
                    onChange={handleChange}
                    className='w-full border border-amber-200 rounded-lg p-3 text-base focus:outline-none focus:ring-2 focus:ring-amber-300 focus:border-transparent bg-white'
                  />
                </div>
              </div>
            </div>

            {/* User Selection (only for USER type) */}
            {formData.voucherType === 'USER' && (
              <div className='bg-emerald-50 p-5 rounded-xl border border-emerald-100'>
                <h3 className='text-lg font-semibold text-emerald-800 mb-4 flex items-center'>
                  <FiUsers className='mr-3 text-xl' /> User Selection
                </h3>

                <div className='flex items-center mb-4'>
                  <input
                    type='checkbox'
                    id='forAllUsers'
                    checked={isForAllUsers}
                    onChange={() => setIsForAllUsers(!isForAllUsers)}
                    className='h-5 w-5 text-emerald-600 focus:ring-emerald-500 border-gray-300 rounded'
                  />
                  <label htmlFor='forAllUsers' className='ml-3 block text-base text-emerald-700'>
                    Apply to all users
                  </label>
                </div>

                {!isForAllUsers && (
                  <div>
                    <label className='block text-sm font-medium text-emerald-600 mb-2'>Select Specific Users</label>
                    <Select
                      isMulti
                      options={userOptions}
                      value={selectedUsers}
                      onChange={handleUserChange}
                      className='text-base border border-emerald-200 rounded-lg'
                      classNamePrefix='select'
                      placeholder='Search and select users...'
                      isSearchable
                      styles={{
                        control: (base) => ({
                          ...base,
                          borderColor: '#a7f3d0',
                          minHeight: '48px',
                          boxShadow: 'none',
                          '&:hover': {
                            borderColor: '#6ee7b7'
                          }
                        }),
                        menu: (base) => ({
                          ...base,
                          fontSize: '1rem'
                        })
                      }}
                    />
                  </div>
                )}
              </div>
            )}
          </div>
        </div>

        {/* Footer Buttons */}
        <div className='flex justify-end gap-4 mt-8'>
          <button
            onClick={onClose}
            className='px-6 py-3 rounded-lg text-base font-medium text-gray-700 hover:bg-gray-100 transition-colors border border-gray-300'
          >
            Cancel
          </button>
          <button
            onClick={handleSubmit}
            className='px-6 py-3 rounded-lg text-base font-medium text-white bg-gradient-to-r from-blue-600 to-purple-600 hover:from-blue-700 hover:to-purple-700 transition-all shadow-md hover:shadow-lg'
          >
            {isEdit ? 'Update Voucher' : 'Create Voucher'}
          </button>
        </div>
      </div>
    </div>
  )
}

export default VoucherModal
