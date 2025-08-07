import { useEffect, useRef, useState } from 'react';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';
import { useNavigate, useParams } from 'react-router-dom';
import chatApi from '../api/chatApi';
import { useSelector } from 'react-redux';
import authApi from '../api/authApi';

export default function Chat() {
  const { movingPlanId } = useParams();

  const [stompClient, setStompClient] = useState(null);
  const [messages, setMessages] = useState([]);
  const [inputmessage, setInputmessage] = useState({ text: '' });
  const [lastReadMessageId, setLastReadMessageId] = useState(null);

  const messagesEndRef = useRef(null);
  // messageId별 DOM 참조를 저장
  const messageRefs = useRef({});
  const chatBoxRef = useRef(null); // chat box 스크롤 감지용

  const navigate = useNavigate();

  const storeNickname = useSelector((state) => state.auth.nickname);

  const chattingName = 'self-start text-xl ml-2 mb-2';
  const chattingDelete =
    'text-gray-500 text-opacity-70 underline cursor-pointer hover:text-primary mr-2';
  const chattingBox =
    'border rounded-xl border-primary border-2 mb-10 h-130 w-full overflow-y-auto p-7';
  const myTimeStyle = 'text-sm mr-2 text-gray-500';
  const otherTimeStyle = 'text-sm ml-2 text-gray-500';
  const myTextStyle = 'border-2 rounded-lg border-gray-400 w-fit max-w-140 px-3 py-1 break-words';
  const otherTextStyle = 'border-2 rounded-lg border-primary w-fit max-w-140 px-3 py-1 break-words';
  const sendTextBox = 'border rounded-xl border-black border-2 px-4 flex-grow h-10';
  const sendButtonBox =
    'cursor-pointer w-[80px] h-10 border-2 rounded-xl text-black hover:bg-white hover:text-primary px-3 ml-3';

  async function fetchChatList() {
    try {
      const response = await chatApi.chatlist(movingPlanId);
      setMessages(response.data.data.chats);
      setLastReadMessageId(response.data.data.lastReadMessageId);
      // ✅ 최초 불러온 메시지 기준 oldestMessageId 설정
      if (chatList.length > 0) {
        setOldestMessageId(chatList[0].messageId);
      }

      console.log(messages);
    } catch (error) {
      if (error.response.data.code == 'NOT_FOUND') {
        navigate('/not-found');
      }
    }
  }

  // wheel 이벤트에서 단순히 messages[0].messageId 기준으로 호출
  useEffect(() => {
    const chatBox = chatBoxRef.current;
    if (!chatBox) return;

    const handleWheel = async () => {
      if (chatBox.scrollTop === 0 && messages.length > 0) {
        await fetchPreviousMessages(messages[0].messageId);
      }
    };

    chatBox.addEventListener('wheel', handleWheel);
    return () => chatBox.removeEventListener('wheel', handleWheel);
  }, [messages]);

  // fetchPreviousMessages 내부에서는 중복 메시지만 걸러서 상태만 갱신
  const fetchPreviousMessages = async (beforeMessageId) => {
    const chatBox = chatBoxRef.current;
    if (!chatBox) return;

    const previousScrollHeight = chatBox.scrollHeight;
    const previousScrollTop = chatBox.scrollTop;

    try {
      const response = await chatApi.previousChatList(movingPlanId, beforeMessageId);
      const newMessages = response.data.data.chats;

      if (newMessages.length === 0) return;

      setMessages((prevMessages) => {
        const existingIds = new Set(prevMessages.map((msg) => msg.messageId));
        const filteredNewMessages = newMessages.filter((msg) => !existingIds.has(msg.messageId));
        const updatedMessages = [...filteredNewMessages, ...prevMessages];

        requestAnimationFrame(() => {
          const newScrollHeight = chatBox.scrollHeight;
          const scrollOffset = newScrollHeight - previousScrollHeight;
          chatBox.scrollTop = previousScrollTop + scrollOffset;
        });

        return updatedMessages;
      });
    } catch (error) {
      console.error('이전 메시지 로딩 실패:', error);
    }
  };

  // 초기 로딩 및 WebSocket 연결
  useEffect(() => {
    fetchChatList();

    const chaturl = import.meta.env.VITE_CHAT_URL;

    const stomp = new Client({
      webSocketFactory: () => new SockJS(chaturl),
    });

    stomp.onConnect = () => {
      stomp.subscribe(`/topic/${movingPlanId}`, (message) => {
        const parsedMessage = JSON.parse(message.body); // JSON 문자열을 객체로
        setMessages((prev) => [...prev, parsedMessage]);
      });

      stomp.subscribe(`/topic/${movingPlanId}/errors`, async (message) => {
        const parsedMessage = JSON.parse(message.body);

        if (parsedMessage.code == 'INVALID_TOKEN') {
          const response = await authApi.reissue();
          const accessToken = response.data.data.accessToken;
          localStorage.setItem('accessToken', accessToken);

          const messageBody = JSON.stringify({
            text: parsedMessage.text,
          });
          stomp.publish({
            destination: `/app/chat/${movingPlanId}`,
            headers: { Authorization: `${accessToken}` },
            body: messageBody,
          });
        }
      });

      setStompClient(stomp);
    };

    stomp.activate(); // WebSocket 활성화

    return () => {
      stomp.deactivate(); // 컴포넌트 언마운트 시 연결 해제
    };
  }, []);

  // 읽은 메시지 저장 및 읽음 표시 스크롤
  useEffect(() => {
    const lastMessage = messages[messages.length - 1];
    if (!lastMessage) return;

    const saveLastReadMessage = async () => {
      try {
        await chatApi.saveLastCursor(movingPlanId, lastMessage.messageId);
      } catch (error) {
        // 에러 무시
      }
    };

    saveLastReadMessage();
    // 위치 마지막으로 읽은 메세지id 부터
    const target = messageRefs.current[lastReadMessageId - 1];
    if (target) {
      target.scrollIntoView({ block: 'center' });
    }
  }, [messages]);

  const textinput = (e) => {
    setInputmessage((prev) => ({ ...prev, text: e.target.value }));
  };

  const sendMessage = (e) => {
    e.preventDefault();

    if (inputmessage.text.trim() === '') return;

    if (stompClient && stompClient.connected) {
      const accessToken = localStorage.getItem('accessToken');

      const messageBody = JSON.stringify({
        text: inputmessage.text,
      });

      stompClient.publish({
        destination: `/app/chat/${movingPlanId}`,
        headers: { Authorization: `${accessToken}` },
        body: messageBody,
      });
    }
    setInputmessage({ text: '' });
  };

  const chatdelete = async () => {
    const isConfirmed = window.confirm('채팅을 삭제하시겠습니까?');

    if (isConfirmed) {
      try {
        await chatApi.chatsdelete(movingPlanId);
        setMessages([]);
      } catch (error) {
        if (error.response.data.code === 'NOT_FOUND') {
          alert('플랜 소유자만 삭제할 수 있습니다.');
        }
      }
    }
  };

  return (
    <div className="mx-auto my-10 max-w-200">
      <div className="flex justify-between">
        <div className={chattingName}>채팅방</div>
        <div className={chattingDelete} onClick={chatdelete}>
          채팅 삭제
        </div>
      </div>
      <div className={chattingBox} ref={chatBoxRef}>
        {messages.map((message, index) => {
          const { messageId, nickname, text, createTime, createDay } = message;
          const previousMessage = index === 0 ? null : messages[index - 1];
          const isOwnMessage = nickname === storeNickname;

          return (
            <div
              className={`w-full mb-3 flex flex-col ${isOwnMessage ? 'items-end' : 'items-start'}`}
              key={messageId}
              ref={(el) => {
                if (el) messageRefs.current[messageId] = el;
              }}
            >
              {(!previousMessage || previousMessage.createDay !== createDay) && (
                <div className="w-full flex flex-col items-center my-5">
                  <div>{createDay}</div>
                  <hr className="w-1/4 mt-2 border-gray-400" />
                </div>
              )}
              {(!previousMessage ||
                previousMessage.nickname !== nickname ||
                previousMessage.createTime !== createTime) && (
                <div className={`${isOwnMessage ? 'mr-1' : 'ml-1'} mb-1`}>{nickname}</div>
              )}
              <div className="flex items-end">
                {isOwnMessage ? (
                  <>
                    <div className={myTimeStyle}>{createTime}</div>
                    <div className={myTextStyle}>{text}</div>
                  </>
                ) : (
                  <>
                    <div className={otherTextStyle}>{text}</div>
                    <div className={otherTimeStyle}>{createTime}</div>
                  </>
                )}
              </div>
            </div>
          );
        })}
        <div ref={messagesEndRef} />
      </div>
      <form className="flex w-full" onSubmit={sendMessage}>
        <input className={sendTextBox} onChange={textinput} value={inputmessage.text} />
        <button className={sendButtonBox}>보내기</button>
      </form>
    </div>
  );
}
