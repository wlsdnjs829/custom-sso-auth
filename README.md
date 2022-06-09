# custom-sso-auth

## ! 참조
> https://www.skyer9.pe.kr/wordpress/?p=2405
>
> https://www.skyer9.pe.kr/wordpress/?p=2294
> 
> https://www.skyer9.pe.kr/wordpress/?p=2367

## 기능 설명
> 기본 베이스 : JWT
> 
> > 인증 방식
> > 1. Redis 내 유효한 토큰 관리 및 인증
> > 1. 만료 여부 및 같은 Client Ip 내 접속 여부 인증
> > 
> > 기본 정책
> > 1. access-token 1시간
> > 1. refresh-token 1일
> > 1. client ip가 다르면 새로운 토큰 발급하도록 처리
> > 1. logout & refresh-token 시 기존 토큰 만료 처리
> > 1. token 내 기본 데이터만 제공하며, 통신은 uid 진행
> > 1. 암호화 방식은 제공된 공개키로만 사용 가능
> 
> swagger-ui 사용 방법
> > 1. 사용자 인증 컨트롤러 > /login > 토큰 발급
> > 1. 우측 중간 Authorize 클릭
> > 1. 발급 받은 accessToken 앞에 Bearer <토큰> 입력
> > 1. 기존 API과 동일하게 사용
> 
> Exception
> > {
> >
> > &#x2001; "timestamp": "2022-06-09T22:36:35.9209721",
> >
> > &#x2001; "status": "BAD_REQUEST",
> >
> > &#x2001; "error": "EXPIRED_REFRESH_TOKEN"
> >
> >}