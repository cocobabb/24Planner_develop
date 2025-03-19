import HeaderLeft from './header/HeaderLeft';
import HeaderRight from './header/HeaderRight';

export default function Header() {
  const headerStyle = 'h-full relative';
  const navStyle = 'flex justify-between h-full p-2 text-2xl';

  return (
    <header className={headerStyle}>
      <nav className={navStyle}>
        <HeaderLeft />
        <HeaderRight />
      </nav>
    </header>
  );
}
