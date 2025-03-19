export default function HomeWithoutAnimation() {
  const flexStyle = 'flex justify-center item-center';
  const largeTextStyle = flexStyle + ' text-6xl font-extrabold p-8';
  const semiLargeTextStyle = flexStyle + ' text-4xl p-8';
  const coloredTextStyle = 'text-primary';

  return (
    <>
      <div className={largeTextStyle}>
        흩어져 있는<span>&nbsp;</span>
        <span className={coloredTextStyle}>이사</span>의 모든 것
      </div>
      <div className={semiLargeTextStyle}>막막한 이사, 이사모음.zip과 깐깐하게 함께 해요!</div>
    </>
  );
}
