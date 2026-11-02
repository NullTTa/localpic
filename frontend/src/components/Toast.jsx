import { useEffect, useState } from 'react';

let push = () => {};

/** 어디서든 toast('메시지') 로 화면 하단에 잠깐 띄운다 */
export const toast = (msg) => push(msg);

export default function ToastHost() {
  const [msg, setMsg] = useState(null);
  useEffect(() => {
    let timer;
    push = (m) => {
      setMsg(m);
      clearTimeout(timer);
      timer = setTimeout(() => setMsg(null), 2200);
    };
    return () => clearTimeout(timer);
  }, []);
  return msg ? <div className="toast">{msg}</div> : null;
}
