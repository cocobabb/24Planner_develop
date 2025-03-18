import HeaderLeft from './header/HeaderLeft';
import HeaderRight from './header/HeaderRight';

export default function Header() {
  return (
    <header className="h-full relative">
      <nav className="flex justify-between h-full p-2 text-2xl">
        <HeaderLeft />
        <HeaderRight />
      </nav>
    </header>
  );
}
