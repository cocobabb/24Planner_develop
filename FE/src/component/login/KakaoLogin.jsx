import kakaoLogin from '../../assets/kakaoLogin.png';

export default function KakaoLogin() {
  const handleSocialLogin = () => {
    // 로컬 환경
    // const apiUrl = import.meta.env.VITE_BASE_URL;
    // window.location.href = `${apiUrl}/api/oauth2/authorization/kakao`;

    // 배포 환경
    window.location.href = '/api/oauth2/authorization/kakao';
  };

  return (
    <img
      src={kakaoLogin}
      alt="카카오 로그인"
      className="cursor-pointer"
      onClick={handleSocialLogin}
    />
  );
}
