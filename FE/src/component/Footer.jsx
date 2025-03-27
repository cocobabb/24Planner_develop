export default function Footer() {
  const footerClass = 'h-18 flex flex-col justify-center items-center';
  const footerTextClass = 'text-md';
  return (
    <footer className={footerClass}>
      <div className={footerTextClass}>24zip.com</div>
    </footer>
  );
}
