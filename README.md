# 톰캣 구현하기

## 요구사항

- [ ] 요청 경로에 따른 올바른 응답을 한다.
    - [ ] 해당 경로에 대한 세부 처리가 있는 경우
        - [ ] 지정된 응답이 있는 경우
            - [ ] 정적 파일
            - [ ] Query String 에 대한 처리

- [ ] 정적 파일에 대한 요청인 경우
    - [ ] 적절한 Content-Type 을 반환한다.
        - [ ] 확장자를 사용
        - [ ] Accept 헤더를 사용

- [x] 요청 메시지를 파싱하여 저장한다.
    - [x] Start-line
    - [x] Headers
    - [x] Body

- [ ] 응답 메시지를 생성한다.
    - [ ] Status Line
    - [ ] Headers
    - [ ] Body
