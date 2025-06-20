import { AxiosError } from 'axios'
import { ApiResponse, CreateOrderParams } from '~/types/order'
import { ProductDetailResponse } from '~/types/product'
import { http } from '~/utils/http'

const apiCreateOrderWithPayment = async ({
  orderDetails,
  userPromotionId,
  addressId,
  amount,
  note,
  expiredTime,
  accessToken
}: CreateOrderParams) => {
  try {
    const config = {
      headers: {
        Authorization: `Bearer ${accessToken}`
      }
    }
    const body = {
      orderDetails,
      userPromotionId,
      addressId,
      amount,
      note,
      expiredTime
    }
    const response = await http.post('/purchase-orders/payment-momo', body, config)
    return response.data
  } catch (error) {
    if (error instanceof AxiosError && error.response) {
      return error.response.data
    }
    return (error as Error).message
  }
}

const apiCheckTransactionStatus = async ({
  orderId,
  accessToken,
  status
}: {
  orderId: string
  accessToken: string
  status?: number // 1: check status from home, 2: check status from order detail
}) => {
  try {
    const config = {
      params: {
        status
      },
      headers: {
        Authorization: `Bearer ${accessToken}`
      }
    }
    const response = await http.get(`/purchase-orders/payment-momo/transaction-status/${orderId}`, config)
    return response.data
  } catch (error) {
    if (error instanceof AxiosError && error.response) {
      return error.response.data
    }
    return (error as Error).message
  }
}

//Order User
const apiGetUserOrders = async ({
  accessToken,
  status
}: {
  accessToken: string
  status: string
}): Promise<ApiResponse> => {
  try {
    const config = {
      headers: {
        Authorization: `Bearer ${accessToken}`
      },
      params: { status }
    }
    const response = await http.get('/purchase-orders/user/orders', config)
    return response.data
  } catch (error) {
    if (error instanceof AxiosError && error.response) {
      return error.response.data
    }
    throw new Error((error as Error).message)
  }
}

//Lấy thông tin sản phẩm đã order
const apiGetProductDetailsByOrder = async ({
  purchaseOrderId,
  accessToken
}: {
  purchaseOrderId: string
  accessToken: string
}): Promise<Promise<ProductDetailResponse>> => {
  const response = await http.get(`/purchase-orders/${purchaseOrderId}/product-details`, {
    headers: {
      Authorization: `Bearer ${accessToken}`
    }
  })
  return response.data
}

//Thống kê số đơn hàng theo trạng thái
const apiGetOrderStatusStatistics = async ({
  accessToken
}: {
  userId: string
  accessToken: string
}): Promise<Promise<ApiResponse>> => {
  const response = await http.get(`/purchase-orders/user/status-statistics`, {
    headers: {
      Authorization: `Bearer ${accessToken}`
    }
  })
  return response.data
}

const apiCancelOrder = async ({
  purchaseOrderId,
  accessToken,
  cancelReason
}: {
  purchaseOrderId: string
  accessToken: string
  cancelReason: string
}) => {
  try {
    const config = {
      headers: {
        Authorization: `Bearer ${accessToken}`
      }
    }
    const body = {
      cancelReason
    }
    const response = await http.post(`/purchase-orders/cancel/${purchaseOrderId}`, body, config)
    return response.data
  } catch (error) {
    if (error instanceof AxiosError && error.response) {
      return error.response.data
    }
    return (error as Error).message
  }
}

const apiEditPurchaseOrder = async ({
  purchaseOrderId,
  orderDetails,
  userPromotionId,
  addressId,
  note,
  expiredTime,
  accessToken
}: {
  purchaseOrderId: string
} & CreateOrderParams) => {
  try {
    const response = await http.put(
      `/purchase-orders/${purchaseOrderId}`,
      {
        orderDetails,
        userPromotionId,
        addressId,
        note,
        expiredTime
      },
      {
        headers: {
          Authorization: `Bearer ${accessToken}`,
          'Content-Type': 'application/json'
        }
      }
    )
    return response.data
  } catch (error) {
    if (error instanceof AxiosError && error.response) {
      return error.response.data
    }
    return (error as Error).message
  }
}

export {
  apiCreateOrderWithPayment,
  apiCheckTransactionStatus,
  apiGetUserOrders,
  apiGetProductDetailsByOrder,
  apiGetOrderStatusStatistics,
  apiCancelOrder,
  apiEditPurchaseOrder
}
