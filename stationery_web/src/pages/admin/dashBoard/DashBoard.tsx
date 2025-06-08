import BuyerProfilePieChart from './component/BuyerProfilePieChart'
import DashBoardStats from './component/DashBoardStats'
import PopularProducts from './component/PopularProducts'
import RecentOrders from './component/RecentOrders'
import TransactionChart from './component/TransactionChart'

function DashBoard() {
  return (
    <div className=''>
      <div className='flex flex-col gap-4'>
        <DashBoardStats />
        <div className='flex flex-row gap-4 w-full'>
          <TransactionChart />
          {/* <BuyerProfilePieChart /> */}
        </div>
        <div className='flex flex-row gap-4 w-full'>
          <RecentOrders />
          <PopularProducts />
        </div>
      </div>
    </div>
  )
}

export default DashBoard
