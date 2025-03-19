import authApi from '../api/authApi';

export default function PlanList() {
  // 테스트
  const onClick = async () => {
    await authApi.test();
  };

  return <button onClick={onClick}>test</button>;
}
