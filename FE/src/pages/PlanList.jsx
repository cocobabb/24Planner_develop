import { useEffect, useState } from 'react';
import planApi from '../api/planApi';
import Plan from '../component/plan/Plan';
import PlanCreator from '../component/plan/PlanCreator';

export default function PlanList() {
  // 상태 관리 데이터
  const [plans, setPlans] = useState([]);

  // 이사 플랜 리스트 조회
  useEffect(() => {
    async function fetchPlans() {
      try {
        const response = await planApi.readPlans();
        const data = response.data.data;

        setPlans(data.movingPlans);
      } catch (error) {}
    }
    fetchPlans();
  }, []);

  // 새 이사 플랜 생성 시 리스트에 추가
  const handleNewPlan = (newPlan) => {
    setPlans((prevPlans) => [newPlan, ...prevPlans]);
  };

  // CSS
  const displayStyle = 'w-300 mx-auto my-5 text-center';
  const titleStyle = 'text-2xl';
  const lineStyle = 'mx-30 mt-5 border-t-2 border-primary';
  const planListContainer = 'flex flex-col items-center px-60 py-10 list-none';
  const emptyText = 'mt-10 text-primary text-2xl';

  return (
    <div className={displayStyle}>
      <div>
        <h2 className={titleStyle}>이사 목록</h2>
        <hr className={lineStyle} />
      </div>
      <ul className={planListContainer}>
        <PlanCreator onPlanCreated={handleNewPlan} />
        {plans.length > 0 ? (
          plans.map((plan) => <Plan key={plan.id} plan={plan} />)
        ) : (
          <p className={emptyText}>새 이사 플랜을 추가해보세요</p>
        )}
      </ul>
    </div>
  );
}
