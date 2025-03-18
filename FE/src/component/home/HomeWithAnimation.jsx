import { useState, useEffect } from 'react';

export default function HomeWithAnimation() {
  const [textClass1, setTextClass1] = useState('text-primary invisible');
  const [textClass2, setTextClass2] = useState('text-primary invisible');
  const [textClass3, setTextClass3] = useState('invisible');
  const [textClass4, setTextClass4] = useState('invisible');
  const [textClass5, setTextClass5] = useState('invisible');

  const flexStyle = 'flex justify-center item-center';

  useEffect(() => {
    const timers = [];
    timers.push(
      setTimeout(() => {
        setTextClass1('text-primary');
      }, 1100),
    );

    timers.push(
      setTimeout(() => {
        setTextClass2('text-primary');
      }, 1600),
    );

    timers.push(
      setTimeout(() => {
        setTextClass3('');
      }, 2100),
    );

    timers.push(
      setTimeout(() => {
        setTextClass4('');
      }, 2600),
    );

    timers.push(
      setTimeout(() => {
        setTextClass5('');
      }, 3600),
    );

    return () => {
      timers.forEach((timer) => {
        clearTimeout(timer);
      });
    };
  }, []);

  return (
    <>
      <div className={`${flexStyle} text-6xl font-extrabold p-8`}>
        <span className="relative animate-main-char1">흩</span>
        <span className="relative animate-main-char2">어</span>
        <span className="relative animate-main-char3">져</span>
        <span>&nbsp;</span>
        <span className="relative animate-main-char4">있</span>
        <span className="relative animate-main-char5">는</span>
        <span>&nbsp;</span>
        <span className={textClass1}>이</span>
        <span className={textClass2}>사</span>
        <span className={textClass3}>의</span>
        <span>&nbsp;</span>
        <span className={textClass4}>모든 것</span>
      </div>
      <div className={`${flexStyle} text-4xl p-8`}>
        <span className={`${textClass5} animate-main-string`}>
          막막한 이사, 이사모음.zip과 깐깐하게 함께 해요!
        </span>
      </div>
    </>
  );
}
