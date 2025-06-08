import { AxiosError } from 'axios'
import { http } from '~/utils/http'

const apiGetMyVoucher = async ({ accessToken }: { accessToken: string }) => {
  try {
    const configs = {
      headers: {
        Authorization: `Bearer ${accessToken}`
      }
    }
    const response = await http.get('/user-promotions', configs)
    return response.data
  } catch (error) {
    if (error instanceof AxiosError && error.response) {
      return error.response.data // Return server error response if available
    }
    return error // Avoid undefined error
  }
}
export { apiGetMyVoucher }
