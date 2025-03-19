import { Navigate } from 'react-router-dom';
import { useSelector } from 'react-redux';

export default function ProtectedLayout({ children }) {
  const isLoggedIn = useSelector((state) => state.auth.isLoggedIn);
  return isLoggedIn ? children : <Navigate to="/" replace />;
}
