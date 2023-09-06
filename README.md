# 톰캣 구현하기

## 현재 구조
```mermaid
    graph TD;
        Tomcat-->Connector;
        Connector-->Http11Processor;
        Http11Processor-->Processor;
```
### Tomcat
- start() 메서드가 호출되면 `connector를 하나 생성`한다.
  - connector를 하나 생성한 이후 `start`한다.
  - connector 객체는 Runnable을 구현한 객체로 쓰레드마다 할당된다
- 이후 `콘솔을 통해 아무 값이나 입력`을 받는 경우에는 `connector를 stop`하고 종료한다. 

### Connector
- ServerSocket과 stopped를 필드로 가지고 있다.
  - 처음 Connector 객체를 생성할 때 ServerSocket를 하나 생성하고,stopped를 false로 둔다.
  - ServerSocket은 클라이언트의 요청을 전달받는(accept) 역할을 한다.
- start 메소드가 호출되면 현재 Connector 객체를 쓰레드에 할당한다.
  - Runnable 인터페이스를 구현했기에 run 메소드를 오버라이드해서 쓰레드의 동작을 설정할 수 있다.
  - stopped가 false이면 계속 connect()를 호출한다.
- ServerSocket이 연결되면 `Http11Processor를 생성`하여 이를 쓰레드에 할당하여 시작한다.
- stop 메소드가 호출되면 stopped를 true로 바꾸고 serverSocket을 닫는다.

### Http11Processor
- 연결이 된다면 inputStream과 outputStream을 생성하여 외부의 요청을 처리한다.
  - inputStream을 통해 클라이언트의 요청을 전달받는다.
  - outputStream을 통해 클라이언트에게 응답한다.

# 요구사항
## 1. HTTP Status Code 302
- 로그인을 한다면 302 상태 코드와 함께 index.html 페이지로 이동한다.
- 로그인에 실패한다면 401 상태 코드와 함께 401.html 페이지로 이동한다.

### 요구사항을 만족시키기 위해 필요한 기능
- [ ] 핸들러를 통해 로그인 여부를 확인할 수 있다.
- [ ] 로그인 여부에 따라 다른 페이지를 전달할 수 있다.

## 이전 미션에서 추가적으로 진행할 요구사항
- [ ] 메인 홈 페이지가 입력되는 경우에는 문자열을 그대로 전달한다. (기존코드로 수정)
  - 웹서버의 리턴 값의 타입이 다양할 수 있다는 것을 학습하기 위해 문자열을 전달했던 것으로 보임
  - 따라서 기존 코드 형태로 다시 변경하고자함
- [ ] 문자열과 file resource를 전달할 수 있다.
