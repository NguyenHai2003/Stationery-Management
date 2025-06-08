interface Promotion {
  promotionId: string
  promoCode: string
  discountType: DiscountType
  discountValue: number
  usageLimit: number
  maxValue: number
  minOrderValue: number
  startDate: string // ISO format e.g. "2025-04-11"
  endDate: string
  createdAt: string
}
interface ProductPromotion {
  productPromotionId: string
  promotion: Promotion
}
interface UserPromotion {
  userPromotionId: string
  promotion: Promotion
}
type DiscountType = 'PERCENTATE' | 'VALUE'

interface SelectedPromotion {
  promotionId: string
  code: string
}

export type { DiscountType, Promotion, ProductPromotion, UserPromotion, SelectedPromotion }
